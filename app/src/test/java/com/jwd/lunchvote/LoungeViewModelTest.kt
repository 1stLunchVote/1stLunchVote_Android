package com.jwd.lunchvote

import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.test.util.MainDispatcherRule
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.usecase.lounge.CreateLoungeUseCase
import com.jwd.lunchvote.domain.usecase.lounge.GetMemberListUseCase
import com.jwd.lunchvote.model.LoungeMember
import com.jwd.lunchvote.ui.lounge.LoungeContract
import com.jwd.lunchvote.ui.lounge.LoungeViewModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoungeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK private lateinit var createLoungeUseCase: CreateLoungeUseCase
    @MockK private lateinit var getMemberListUseCase: GetMemberListUseCase
    @MockK private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: LoungeViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        every { savedStateHandle.get<LoungeContract.LoungeState>("viewState") } returns null
        every { savedStateHandle.get<String?>("id") } returns "test3"

        viewModel = LoungeViewModel(
                createLoungeUseCase,
                getMemberListUseCase,
                savedStateHandle
            )
    }

    @Test
    fun `멤버 리스트 제대로 업데이트 되는지 확인`() = runTest {
        // Given
        val memberList = listOf(
            Member(
                nickname = "test",
                profileImage = "testImage",
                isReady = true,
                joinedTime = "2023-06-02 00:00:00"
            ),
        )
        every { getMemberListUseCase.invoke(any())} returns flowOf(memberList)

        // When
        // viewModel 초기에 설정될 때 이루어지는지 체크
        viewModel.initialize()
        advanceUntilIdle()

        // Then
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