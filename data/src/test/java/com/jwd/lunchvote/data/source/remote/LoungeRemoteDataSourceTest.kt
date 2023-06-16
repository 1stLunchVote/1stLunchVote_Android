package com.jwd.lunchvote.data.source.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.jwd.lunchvote.remote.source.LoungeRemoteDataSourceImpl
import com.jwd.lunchvote.domain.entity.Member
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoungeRemoteDataSourceTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @MockK private lateinit var db: FirebaseDatabase
    @MockK private lateinit var auth: FirebaseAuth
    private lateinit var loungeRemoteDataSource: com.jwd.lunchvote.remote.source.LoungeRemoteDataSourceImpl

    @Before
    fun setup(){
        MockKAnnotations.init(this, relaxed = true)
        loungeRemoteDataSource = spyk(
            com.jwd.lunchvote.remote.source.LoungeRemoteDataSourceImpl(
                auth,
                db,
                testDispatcher
            )
        )
    }

    @Test
    fun getMemberList_initial_load() = runTest(testDispatcher) {
        // Given
        val testId = "KqND4zmJ59"
        val testMemberList =
            listOf(
                Member(
                    uid = "testUid",
                    nickname = "testNickname",
                    profileImage = "testProfileImage",
                    ready = true,
                    joinedTime = "testJoinedTime"
                )

        )

        // When
        val res = loungeRemoteDataSource.getMemberList(testId).first()
        // Then
        Assert.assertEquals(
            testMemberList,
            res
        )
    }
}