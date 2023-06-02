package com.jwd.lunchvote

import android.os.Parcelable
import com.jwd.lunchvote.core.test.base.BaseStateViewModelTest
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.usecase.lounge.CreateLoungeUseCase
import com.jwd.lunchvote.domain.usecase.lounge.GetMemberListUseCase
import com.jwd.lunchvote.model.LoungeMember
import com.jwd.lunchvote.ui.lounge.LoungeViewModel
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoungeViewModelTest : BaseStateViewModelTest() {
    override val initialState: Parcelable? = null

    @MockK private lateinit var createLoungeUseCase: CreateLoungeUseCase
    @MockK private lateinit var getMemberListUseCase: GetMemberListUseCase

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
                isReady = true,
                joinedTime = "2023-06-02 00:00:00"
            ),
        )
        every { createLoungeUseCase.invoke() } returns flowOf("test1")
        every { getMemberListUseCase.invoke(any())} returns flowOf(memberList)

        // When
        // viewModel 초기에 설정될 때 이루어지는지 체크

        // lateinit var로 viewModel 선언 시 생성자 호출 Test에서 이루어지지 않음
        val viewModel = spyk(
            LoungeViewModel(createLoungeUseCase, getMemberListUseCase, savedStateHandle),
            recordPrivateCalls = true
        )
        advanceUntilIdle()

        Assert.assertEquals(
            listOf(
                LoungeMember(
                    profileImage = "testImage",
                    isReady = true
                )
            ),
            viewModel.viewState.value.memberList,
        )
    }
}