package com.jwd.lunchvote.remote.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.firebase.functions.FirebaseFunctions
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import com.jwd.lunchvote.data.model.LoungeChatData
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.data.model.type.MessageDataType
import com.jwd.lunchvote.data.source.remote.LoungeRemoteDataSource
import com.jwd.lunchvote.remote.mapper.LoungeChatRemoteMapper
import com.jwd.lunchvote.remote.mapper.MemberRemoteMapper
import com.jwd.lunchvote.remote.mapper.type.MessageRemoteTypeMapper
import com.jwd.lunchvote.remote.model.LoungeChatRemote
import com.jwd.lunchvote.remote.model.MemberRemote
import com.jwd.lunchvote.remote.util.getValueEventFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

class LoungeRemoteDataSourceImpl @Inject constructor(
    private val functions: FirebaseFunctions,
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase,
    @Dispatcher(IO) private val dispatcher: CoroutineDispatcher
) : LoungeRemoteDataSource {
    override suspend fun checkLoungeExist(
        loungeId: String
    ): Boolean = withContext(dispatcher) {
        val roomRef = db.getReference("$Lounge/${loungeId}")
        roomRef.get().await().exists()
    }

    override suspend fun createLounge(): String = withContext(dispatcher) {
        val user = checkNotNull(auth.currentUser)
        val loungeId = UUID.randomUUID().toString()

        val joinedAt = ZonedDateTime.now().toString()


        val roomRef = db.getReference("$Lounge/${loungeId}")

        roomRef.child(Member).child(user.uid).setValue(
            MemberRemote(
                user.uid, loungeId, user.displayName.toString(), user.photoUrl.toString(),
                "joined", true, joinedAt
            )
        ).await()

        return@withContext loungeId
    }

    override suspend fun joinLounge(loungeId: String) {
        withContext(dispatcher) {
            val roomRef = db.getReference("$Lounge/${loungeId}")
            val user = checkNotNull(auth.currentUser)
            val joinedAt = ZonedDateTime.now().toString()

            roomRef.child(Member).child(user.uid).setValue(
                MemberRemote(
                    user.uid, loungeId, user.displayName.toString(), user.photoUrl.toString(),
                    "joined", false, joinedAt
                )
            ).await()
        }
    }

    override fun getMemberList(loungeId: String): Flow<List<MemberData>> {
        val memberRef = db.getReference("$Lounge/${loungeId}").child(Member)

        return memberRef.getValueEventFlow<HashMap<String, MemberRemote>>()
            .map { it.values.map(MemberRemoteMapper::mapToRight).sortedBy { member -> member.joinedAt } }
            .flowOn(dispatcher)
    }

    override fun getChatList(loungeId: String): Flow<List<LoungeChatData>> {
        val chatRef = db.getReference(Chat).child(loungeId)

        return chatRef.getValueEventFlow<HashMap<String, LoungeChatRemote>>()
            .map { it.values.map(LoungeChatRemoteMapper::mapToRight).sortedBy { chat -> chat.createdAt } }
            .flowOn(dispatcher)
    }

    override suspend fun sendChat(
        id: String, loungeId: String, content: String?, type: MessageDataType,
    ) {
        withContext(dispatcher) {
            val name = auth.currentUser?.displayName?.ifBlank { "익명" }
            val messageType = type.let(MessageRemoteTypeMapper::mapToLeft)
            val chatContent = content ?: when (messageType) {
                1 -> Chat_Create
                2 -> "$name $Chat_Join"
                else -> "$name $Chat_Exit"
            }

            val data = JSONObject().apply {
                put("id", id)
                put("loungeId", loungeId)
                put("userId", auth.currentUser?.uid)
                put("userProfile", auth.currentUser?.photoUrl.toString())
                put("message", chatContent)
                put("type", messageType)
                put("createdAt", LocalDateTime.now().toString())
            }

            functions.getHttpsCallable("sendChat").call(data).await()
        }
    }

    override suspend fun updateReady(
        uid: String, loungeId: String
    ) {
        withContext(dispatcher){
            val statusRef = db.getReference("$Lounge/${loungeId}").child(Member).child(uid).child(Status)
            val cur = statusRef.get().await().getValue<String>()
            statusRef.setValue(if (cur == "joined") "ready" else "joined").await()
        }
    }

    override suspend fun exitLounge(
        uid: String, loungeId: String
    ){
        withContext(dispatcher){
            val memberRef = db.getReference("$Lounge/${loungeId}").child(Member).child(uid)
            memberRef.setValue(null).await()
        }
    }


    companion object{
        const val Lounge = "Lounge"
        const val Chat = "Chat"
        const val Room_Length = 10
        const val Member = "members"
        const val Status = "status"
        const val Chat_Create = "투표 방이 생성되었습니다."
        const val Chat_Join = "님이 입장했습니다."
        const val Chat_Exit = "님이 퇴장했습니다."
    }
}