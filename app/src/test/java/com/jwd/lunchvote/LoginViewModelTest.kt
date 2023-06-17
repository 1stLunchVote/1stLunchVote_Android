package com.jwd.lunchvote

import android.os.Parcelable
import com.google.firebase.auth.FirebaseAuth
import com.jwd.lunchvote.core.test.base.BaseStateViewModelTest
import com.jwd.lunchvote.domain.usecase.login.KakaoLoginUseCase
import com.jwd.lunchvote.ui.login.LoginContract
import com.jwd.lunchvote.ui.login.LoginViewModel
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class LoginViewModelTest : BaseStateViewModelTest(){
    override val initialState: Parcelable? = null

    @MockK private lateinit var kakaoLoginUseCase: KakaoLoginUseCase
    @MockK private lateinit var auth: FirebaseAuth

    private lateinit var viewModel: LoginViewModel

    override fun initSetup() {
        viewModel = spyk(
            LoginViewModel(savedStateHandle, auth, kakaoLoginUseCase),
            recordPrivateCalls = true
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    // 네이밍 : MethodName_StateUnderTest_ExpectedBehavior
    fun kakaoLogin_error_onLoginFailure() = runTest{
        // given
        val error = Throwable("로그인에 실패하였습니다.")
        every { kakaoLoginUseCase(any()) } returns flow { throw error }

        // when
        viewModel.handleEvents(LoginContract.LoginEvent.ProcessKakaoLogin("test"))
        advanceUntilIdle()

        // then
        // 로그인 오류 처리 하는 함수 호출되는지 체크
        verify(exactly = 1) { viewModel["onLoginFailure"](error.message) }
        // 로딩 상태가 false로 변경되는지 체크
        Assert.assertEquals(false, viewModel.viewState.value.isLoading)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun kakaoLogin_success_navigateToHome() = runTest {
        // given
        every { kakaoLoginUseCase(any()) } returns flowOf(Unit)

        // when
        viewModel.handleEvents(LoginContract.LoginEvent.ProcessKakaoLogin("test"))
        advanceUntilIdle()

        // then
        // 홈으로 이동하는 함수 호출되는지 체크
        verify(exactly = 1) { viewModel["sendSideEffect"](LoginContract.LoginSideEffect.NavigateToHome)}
    }
}