package com.jwd.lunchvote

import android.os.Parcelable
import com.jwd.lunchvote.core.test.base.BaseStateViewModelTest
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.usecase.lounge.CreateLoungeUseCase
import com.jwd.lunchvote.domain.usecase.lounge.GetChatListUseCase
import com.jwd.lunchvote.domain.usecase.lounge.GetMemberListUseCase
import com.jwd.lunchvote.domain.usecase.lounge.JoinLoungeUseCase
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

    override fun initSetup() {
        every { savedStateHandle.get<String?>("id") } returns "test3"
    }

    @Test
    fun state_initial_memberListLoad() = runTest {
        // Given
        val memberList = listOf(
            Member(
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
            LoungeViewModel(createLoungeUseCase, joinLoungeUseCase, getMemberListUseCase, getChatListUseCase, savedStateHandle),
            recordPrivateCalls = true
        )
        advanceUntilIdle()

        Assert.assertEquals(
            listOf(
                MemberUIModel(
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
                    nickname = "test",
                    profileImage = "testImage",
                    ready = true,
                    joinedTime = "2023-06-02 00:00:00"
                ),
            ))

            delay(1000)

            emit(listOf(
                Member(
                    nickname = "test",
                    profileImage = "testImage",
                    ready = true,
                    joinedTime = "2023-06-02 00:00:00"
                ),
                Member(
                    nickname = "test2",
                    profileImage = "testImage2",
                    ready = true,
                    joinedTime = "2023-06-02 00:01:00"
                ),
            ))
        }

        // when
        val viewModel = spyk(
            LoungeViewModel(createLoungeUseCase, joinLoungeUseCase, getMemberListUseCase, getChatListUseCase, savedStateHandle),
            recordPrivateCalls = true
        )

        delay(500)
        // then
        Assert.assertEquals(
            listOf(
                MemberUIModel(
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
                    profileImage = "testImage",
                    isReady = true
                ),
                MemberUIModel(
                    profileImage = "testImage2",
                    isReady = true
                )
            ),
            viewModel.viewState.value.memberList
        )
    }
}