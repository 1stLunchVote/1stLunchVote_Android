package com.jwd.lunchvote.local.source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.auth.FirebaseAuth
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import com.jwd.lunchvote.data.model.LoungeChatData
import com.jwd.lunchvote.data.model.MemberData
import com.jwd.lunchvote.data.model.type.MemberStatusDataType
import com.jwd.lunchvote.data.model.type.MessageDataType
import com.jwd.lunchvote.data.model.type.SendStatusDataType
import com.jwd.lunchvote.data.source.local.LoungeLocalDataSource
import com.jwd.lunchvote.local.room.dao.ChatDao
import com.jwd.lunchvote.local.room.dao.LoungeDao
import com.jwd.lunchvote.local.room.dao.MemberDao
import com.jwd.lunchvote.local.room.entity.ChatEntity
import com.jwd.lunchvote.local.room.entity.LoungeEntity
import com.jwd.lunchvote.local.room.mapper.ChatEntityMapper
import com.jwd.lunchvote.local.room.mapper.MemberEntityMapper
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
    private val dataStore: DataStore<Preferences>,
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
    ): Flow<List<MemberData>>
        = memberDao.getAllMember(loungeId).map { it.map(MemberEntityMapper::mapToRight) }.flowOn(dispatcher)

    override suspend fun putMemberList(
        memberList: List<MemberData>, loungeId: String
    ) {
        withContext(dispatcher) {
            loungeDao.insertLounge(LoungeEntity(loungeId))

            memberDao.deleteAllMember(loungeId)
            memberDao.insertAllMember(memberList.map(MemberEntityMapper::mapToLeft))
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
        val status = memberDao.getMemberStatus(uid, loungeId)
        memberDao.updateMemberReady(
            uid, loungeId,
            if (status == MemberStatusDataType.READY) MemberStatusDataType.JOINED else MemberStatusDataType.READY
        )
    }

    override suspend fun deleteAllChat(
        loungeId: String
    ) = withContext(dispatcher){
        chatDao.deleteAllChat(loungeId)
    }

    override suspend fun updateCurrentLounge(loungeId: String) {
        dataStore.edit { pref ->
            pref[CURRENT_LOUNGE] = loungeId
        }
    }

    override suspend fun deleteCurrentLounge() {
        dataStore.edit {
            it.remove(CURRENT_LOUNGE)
        }
    }

    override fun getCurrentLounge(): Flow<String?> {
        return dataStore.data.map { pref ->
            pref[CURRENT_LOUNGE]
        }
    }

    companion object {
        private val CURRENT_LOUNGE = stringPreferencesKey("currentLounge")
    }
}