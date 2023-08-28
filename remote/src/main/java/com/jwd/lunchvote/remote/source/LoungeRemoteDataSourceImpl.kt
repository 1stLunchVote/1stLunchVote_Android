package com.jwd.lunchvote.remote.source

import android.content.res.Resources.NotFoundException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.functions.FirebaseFunctions
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import com.jwd.lunchvote.data.source.remote.LoungeRemoteDataSource
import com.jwd.lunchvote.data.util.createRandomString
import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.remote.util.getValueEventFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

class LoungeRemoteDataSourceImpl @Inject constructor(
    private val functions: FirebaseFunctions,
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase,
    @Dispatcher(IO) private val dispatcher: CoroutineDispatcher
) : LoungeRemoteDataSource {
    override fun checkLoungeExist(
        loungeId: String
    ): Flow<Boolean> = flow {
        val roomRef = db.getReference("$Room/${loungeId}")
        emit(roomRef.get().await().exists())

    }.flowOn(dispatcher)

    override fun createLounge(): Flow<String?> = callbackFlow {
        val roomId = createRandomString(Room_Length)

        val roomRef = db.getReference("$Room/${roomId}")
        auth.currentUser?.let{ user ->
            roomRef.setValue(mapOf("owner" to user.uid))
                .addOnSuccessListener {
                    trySend(roomId)
                }
                .addOnFailureListener {
                    Timber.e(it)
                    trySend(null)
                }
            addMember(roomRef, user.uid, user.displayName.toString(), user.photoUrl.toString(), true)
        }
        awaitClose()
    }.flowOn(dispatcher)

    override fun joinLounge(loungeId: String): Flow<Unit?> = flow {
        val roomRef = db.getReference("$Room/${loungeId}")

        auth.currentUser?.let {user ->
            val userExist = roomRef.child(Member).child(user.uid).get().await().getValue<Member>()

            addMember(roomRef, user.uid, user.displayName.toString(), user.photoUrl.toString(), false, userExist?.joinedTime)

            emit(if(userExist != null) null else Unit)
        }
    }.flowOn(dispatcher)

    override fun getMemberList(loungeId: String): Flow<List<Member>> {
        val memberRef = db.getReference("$Room/${loungeId}").child(Member)

        return memberRef.getValueEventFlow<HashMap<String, Member>>()
            .map { it.values.toList().sortedBy { m -> m.joinedTime } }.flowOn(dispatcher)
    }

    override fun getChatList(loungeId: String): Flow<List<LoungeChat>> {
        val chatRef = db.getReference("$Room/${loungeId}").child(Chat)

        return chatRef.getValueEventFlow<ArrayList<LoungeChat>>().flowOn(dispatcher)
    }

    override fun sendChat(
        loungeId: String, content: String?, messageType: Int
    ): Flow<Unit> = flow {
        val name = auth.currentUser?.displayName?.ifBlank { "익명" }
        val chatContent = content ?: when (messageType) {
            1 -> Chat_Create
            2 -> "$name $Chat_Join"
            else -> "$name $Chat_Exit"
        }

        val data = JSONObject().apply {
            put("loungeId", loungeId)
            put("sender", auth.currentUser?.uid)
            put("senderProfile", auth.currentUser?.photoUrl.toString())
            put("content", chatContent)
            put("createdAt", LocalDateTime.now().toString())
            put("messageType", messageType)
        }

        functions.getHttpsCallable("sendChat").call(data).await()
        emit(Unit)

    }.flowOn(dispatcher)

    override fun updateReady(
        uid: String, loungeId: String
    ): Flow<Unit> = flow {
        val readyRef = db.getReference("$Room/${loungeId}").child(Member).child(uid).child(Ready)
        // 네트워크 연결이 안되어서 오류가 난 경우, 네트워크 연결되면 바로 자동 요청
        val cur = readyRef.get().await().getValue<Boolean>()
        readyRef.setValue(cur?.not())
        emit(Unit)
    }.flowOn(dispatcher)

    override suspend fun exitLounge(
        uid: String, loungeId: String
    ){
        withContext(dispatcher){
            val memberRef = db.getReference("$Room/${loungeId}").child(Member).child(uid)
            memberRef.setValue(null).await()
        }
    }

    private fun addMember(
        roomRef: DatabaseReference, uid: String, displayName: String,
        photoUrl: String?, isOwner: Boolean, joinedTime: String? = null
    ) {
        val currentTime = LocalDateTime.now().toString()

        // 나갔다 들어오면 무조건 준비 상태 false
        roomRef.child(Member).child(uid).setValue(
            Member(uid, displayName, photoUrl, false, isOwner, joinedTime ?: currentTime)
        )
    }

    companion object{
        const val Room = "rooms"
        const val Room_Length = 10
        const val Member = "members"
        const val Ready = "ready"
        const val Chat = "chats"
        const val Chat_Create = "투표 방이 생성되었습니다."
        const val Chat_Join = "님이 입장했습니다."
        const val Chat_Exit = "님이 퇴장했습니다."
    }
}