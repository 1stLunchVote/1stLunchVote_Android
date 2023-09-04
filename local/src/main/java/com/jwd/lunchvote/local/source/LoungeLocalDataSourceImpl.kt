package com.jwd.lunchvote.local.source

import com.google.firebase.auth.FirebaseAuth
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import com.jwd.lunchvote.data.model.LoungeChatData
import com.jwd.lunchvote.data.model.type.MessageDataType
import com.jwd.lunchvote.data.model.type.SendStatusDataType
import com.jwd.lunchvote.data.source.local.LoungeLocalDataSource
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.local.room.dao.ChatDao
import com.jwd.lunchvote.local.room.dao.LoungeDao
import com.jwd.lunchvote.local.room.dao.MemberDao
import com.jwd.lunchvote.local.room.entity.ChatEntity
import com.jwd.lunchvote.local.room.entity.LoungeEntity
import com.jwd.lunchvote.local.room.entity.MemberEntity
import com.jwd.lunchvote.local.room.mapper.ChatEntityMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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
        loungeId: String,
    ): Flow<List<LoungeChatData>>
        = chatDao.getAllChat(loungeId).map { it.map(ChatEntityMapper::mapToRight) }

    override suspend fun putChatList(
        chatList: List<LoungeChatData>, loungeId: String
    ) = withContext(dispatcher) {
        loungeDao.insertLounge(LoungeEntity(loungeId))

        chatDao.deleteAllChat(loungeId)
        chatDao.insertAllChat(chatList.map(ChatEntityMapper::mapToLeft))
    }

    override fun getMemberList(
        loungeId: String
    ): Flow<List<Member>>
        = memberDao.getAllMember(loungeId).map { it.map(MemberEntity::toDomain) }.flowOn(dispatcher)

    override suspend fun putMemberList(
        memberList: List<Member>, loungeId: String
    ) {
        withContext(dispatcher) {
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
    }

    override suspend fun insertChat(
        id: String, loungeId: String, content: String, type: MessageDataType
    ) {
        withContext(dispatcher){
            auth.currentUser?.let {user ->
                chatDao.insertChat(
                    ChatEntity(
                        id = id,
                        userId = user.uid,
                        userProfile = user.photoUrl.toString(),
                        message = content,
                        messageType = type,
                        createdAt = System.currentTimeMillis().toString(),
                        loungeId = loungeId,
                        sendStatus = SendStatusDataType.SENDING
                    )
                )
            }
        }
    }

    override suspend fun deleteChat(
        loungeId: String
    ) = withContext(dispatcher){
        chatDao.deleteSendingChat(loungeId)
    }

    override suspend fun updateMemberReady(
        uid: String, loungeId: String
    ) = withContext(dispatcher){
        memberDao.updateMemberReady(uid, loungeId)
    }

    override suspend fun deleteAllChat(
        loungeId: String
    ) = withContext(dispatcher){
        chatDao.deleteAllChat(loungeId)
    }
}