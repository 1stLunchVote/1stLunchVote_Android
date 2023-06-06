package com.jwd.lunchvote.data.source.remote.lounge

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import com.jwd.lunchvote.data.util.createRandomString
import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.entity.Member
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

class LoungeRemoteDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase,
    @Dispatcher(IO) private val dispatcher: CoroutineDispatcher
) : LoungeRemoteDataSource {

    override fun createLounge(): Flow<String?> = callbackFlow {
        val roomId = createRandomString(Room_Length)

        val roomRef = db.getReference("${Room}/${roomId}")
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

    override fun joinLounge(loungeId: String): Flow<Unit> = flow {
        val roomRef = db.getReference("${Room}/${loungeId}")

        auth.currentUser?.let {user ->
            val userExist = roomRef.child(Member).child(user.uid).get().await().exists()
            if (userExist) emit(Unit)
            else {
                emit(
                    addMember(roomRef, user.uid, user.displayName.toString(), user.photoUrl.toString(), false)
                )
            }
        }
    }.flowOn(dispatcher)

    override fun getMemberList(loungeId: String): Flow<List<Member>> = callbackFlow {
        val memberRef = db.getReference("${Room}/${loungeId}").child(Member)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val res = snapshot.getValue<HashMap<String, Member>>()
                    Timber.e("get Value : $res")
                    res?.let { trySend(res.values.toList().sortedBy { it.joinedTime }) }
                } catch (e: Exception){
                    Timber.e("error: ${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("$error")
                close(Throwable(error.message))
            }
        }

        memberRef.addValueEventListener(listener)

        awaitClose{ memberRef.removeEventListener(listener) }
    }

    override fun getChatList(loungeId: String): Flow<List<LoungeChat>> = callbackFlow {
        val chatRef = db.getReference("${Room}/${loungeId}").child(Chat)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val res = snapshot.getValue<ArrayList<LoungeChat>>()
                    Timber.e("chatList : $res")
                    res?.let { trySend(res)}
                } catch (e: Exception){
                    Timber.e("error: ${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("$error")
            }
        }

        chatRef.addValueEventListener(listener)

        awaitClose { chatRef.removeEventListener(listener) }
    }.flowOn(dispatcher)

    private suspend fun addMember(
        roomRef: DatabaseReference, uid: String, displayName: String,
        photoUrl: String?, create: Boolean,
    ) {
        val currentTime = LocalDateTime.now().toString()

        roomRef.child(Member).child(uid).setValue(
            Member(uid, displayName, photoUrl, false, create, currentTime)
        )
        val chatId = roomRef.child(Chat).get().await().childrenCount
        roomRef.child(Chat).child(chatId.toString()).setValue(
            if (create)
                LoungeChat(uid, photoUrl, Chat_Create, 1, currentTime)
            else
                LoungeChat(uid, photoUrl, "$displayName $Chat_Join", 1, currentTime)
        )
    }

    companion object{
        const val Room = "rooms"
        const val Room_Length = 10
        const val Member = "members"
        const val Chat = "chats"
        const val Chat_Create = "투표 방이 생성되었습니다."
        const val Chat_Join = "님이 입장했습니다."
    }
}