package com.jwd.lunchvote

import android.os.Parcelable
import com.google.firebase.auth.FirebaseAuth
import com.jwd.lunchvote.core.test.base.BaseStateViewModelTest
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.usecase.CreateLoungeUseCase
import com.jwd.lunchvote.domain.usecase.GetChatListUseCase
import com.jwd.lunchvote.domain.usecase.GetMemberListUseCase
import com.jwd.lunchvote.domain.usecase.JoinLoungeUseCase
import com.jwd.lunchvote.model.MemberUIModel
import com.jwd.lunchvote.ui.lounge.LoungeViewModel
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoungeViewModelTest : BaseStateViewModelTest() {
    override val initialState: Parcelable? = null

    @MockK private lateinit var createLoungeUseCase: CreateLoungeUseCase
    @MockK private lateinit var getMemberListUseCase: GetMemberListUseCase
    @MockK private lateinit var joinLoungeUseCase: JoinLoungeUseCase
    @MockK private lateinit var getChatListUseCase: GetChatListUseCase
    @MockK private lateinit var auth: FirebaseAuth

    override fun initSetup() {
        every { savedStateHandle.get<String?>("id") } returns "test1"

        every { joinLoungeUseCase(any()) } returns flowOf(Unit)
        every { getChatListUseCase(any()) } returns flowOf()
    }

    @Test
    fun state_initial_memberListLoad() = runTest {
        // Given
        val memberList = listOf(
            Member(
                uid = "uid1",
                nickname = "test",
                profileImage = "testImage",
                ready = true,
                joinedTime = "2023-06-02 00:00:00"
            ),
        )
        every { createLoungeUseCase.invoke() } returns flowOf("test1")
        every { getMemberListUseCase.invoke(any())} returns flowOf(memberList)

        // When
        // viewModel 초기에 설정될 때 이루어지는지 체크

        // lateinit var로 viewModel 선언 시 생성자 호출 Test에서 이루어지지 않음
        val viewModel = spyk(
            LoungeViewModel(createLoungeUseCase, joinLoungeUseCase, getMemberListUseCase, getChatListUseCase, auth, savedStateHandle),
            recordPrivateCalls = true
        )
        advanceUntilIdle()

        Assert.assertEquals(
            listOf(
                MemberUIModel(
                    uid = "uid1",
                    profileImage = "testImage",
                    isReady = true
                )
            ),
            viewModel.viewState.value.memberList,
        )
    }

    @Test
    fun getMemberList_flow_loadTest() = runTest {
        // given
        every { getMemberListUseCase.invoke(any())} returns flow {
            emit(listOf(
                Member(
                    uid = "uid1",
                    nickname = "test",
                    profileImage = "testImage",
                    ready = true,
                    joinedTime = "2023-06-02 00:00:00"
                ),
            ))

            delay(1000)

            emit(listOf(
                Member(
                    uid = "uid1",
                    nickname = "test",
                    profileImage = "testImage",
                    ready = true,
                    joinedTime = "2023-06-02 00:00:00"
                ),
                Member(
                    uid = "uid2",
                    nickname = "test2",
                    profileImage = "testImage2",
                    ready = true,
                    joinedTime = "2023-06-02 00:01:00"
                ),
            ))
        }

        // when
        val viewModel = spyk(
            LoungeViewModel(createLoungeUseCase, joinLoungeUseCase, getMemberListUseCase, getChatListUseCase, auth, savedStateHandle),
            recordPrivateCalls = true
        )

        delay(500)
        // then
        Assert.assertEquals(
            listOf(
                MemberUIModel(
                    uid = "uid1",
                    profileImage = "testImage",
                    isReady = true
                )
            ),

            viewModel.viewState.value.memberList
        )

        delay(600)

        Assert.assertEquals(
            listOf(
                MemberUIModel(
                    uid = "uid1",
                    profileImage = "testImage",
                    isReady = true
                ),
                MemberUIModel(
                    uid = "uid2",
                    profileImage = "testImage2",
                    isReady = true
                )
            ),
            viewModel.viewState.value.memberList
        )
    }
}