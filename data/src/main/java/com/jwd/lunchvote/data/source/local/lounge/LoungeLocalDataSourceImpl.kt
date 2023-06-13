package com.jwd.lunchvote.data.source.local.lounge

import com.google.firebase.auth.FirebaseAuth
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import com.jwd.lunchvote.data.room.dao.ChatDao
import com.jwd.lunchvote.data.room.dao.LoungeDao
import com.jwd.lunchvote.data.room.dao.MemberDao
import com.jwd.lunchvote.data.room.entity.ChatEntity
import com.jwd.lunchvote.data.room.entity.LoungeEntity
import com.jwd.lunchvote.data.room.entity.MemberEntity
import com.jwd.lunchvote.domain.entity.LoungeChat
import com.jwd.lunchvote.domain.entity.Member
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoungeLocalDataSourceImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val loungeDao: LoungeDao,
    private val memberDao: MemberDao,
    private val auth: FirebaseAuth,
    @Dispatcher(IO) private val dispatcher: CoroutineDispatcher
): LoungeLocalDataSource {
    override fun getChatList(
        loungeId: String
    ): Flow<List<ChatEntity>> = chatDao.getAllChat(loungeId).flowOn(dispatcher)

    override suspend fun putChatList(
        chatList: List<LoungeChat>, loungeId: String
    ) = withContext(dispatcher){
        loungeDao.insertLounge(LoungeEntity(loungeId))

        chatDao.deleteAllChat(loungeId)
        chatDao.insertAllChat(chatList.map {
            ChatEntity(
                it.chatId,
                it.sender.orEmpty(),
                it.senderProfile.orEmpty(),
                it.content.orEmpty(),
                it.messageType,
                it.createdAt.orEmpty(),
                loungeId
            )
        })
    }

    override fun getMemberList(
        loungeId: String
    ): Flow<List<MemberEntity>> = memberDao.getAllMember(loungeId).flowOn(dispatcher)

    override suspend fun putMemberList(
        memberList: List<Member>, loungeId: String
    ) = withContext(dispatcher){
        loungeDao.insertLounge(LoungeEntity(loungeId))

        memberDao.deleteAllMember(loungeId)
        memberDao.insertAllMember(memberList.map {
            MemberEntity(
                it.uid ?: return@withContext,
                it.profileImage,
                it.nickname.orEmpty(),
                it.ready,
                it.owner,
                it.joinedTime.orEmpty(),
                loungeId,
            )
        })
    }

    override fun insertChat(
        loungeId: String, content: String, type: Int
    ): Flow<Unit> = flow {
        auth.currentUser?.let {user ->
            val count = chatDao.getAllChat(loungeId).first().size

            chatDao.insertChat(
                ChatEntity(
                    chatId = count.toLong(),
                    sender = user.uid,
                    senderProfile = user.photoUrl.toString(),
                    content = content,
                    messageType = type,
                    createdAt = System.currentTimeMillis().toString(),
                    loungeId = loungeId,
                    sendStatus = 1
                )
            )
            emit(Unit)
        }
    }.flowOn(dispatcher)

    override fun deleteChat(
        loungeId: String
    ): Flow<Unit> = flow {
        emit(chatDao.deleteSendingChat(loungeId))
    }.flowOn(dispatcher)

    override fun updateMemberReady(
        uid: String, loungeId: String
    ): Flow<Unit> = flow {
        emit(memberDao.updateMemberReady(uid, loungeId))
    }.flowOn(dispatcher)
}