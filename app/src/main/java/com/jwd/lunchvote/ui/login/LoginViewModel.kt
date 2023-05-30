package com.jwd.lunchvote.ui.login

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.functions.FirebaseFunctions
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.KakaoLoginUseCase
import com.jwd.lunchvote.ui.login.LoginContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val auth: FirebaseAuth,
    private val kakaoLoginUseCase: KakaoLoginUseCase
): BaseStateViewModel<LoginState, LoginEvent, LoginReduce, LoginSideEffect>(savedStateHandle){
    override fun createInitialState(savedState: Parcelable?): LoginState {
        return savedState as? LoginState ?: LoginState()
    }

    private fun googleLogin(account: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if(task.isSuccessful){
                sendSideEffect(LoginSideEffect.NavigateToHome)
            } else {
                onLoginFailure("로그인에 실패하였습니다.")
            }
        }
    }

    private fun kakaoLogin(accessToken: String){
        kakaoLoginUseCase(accessToken)
            .catch {
                onLoginFailure("로그인에 실패하였습니다.")
            }
            .onEach {
                sendSideEffect(LoginSideEffect.NavigateToHome)
            }
            .launchIn(viewModelScope)
    }

    override fun handleEvents(event: LoginEvent) {
        when(event){
            is LoginEvent.SetEmail -> {
                updateState(LoginReduce.UpdateEmail(event.email))
            }
            is LoginEvent.SetPwd -> {
                updateState(LoginReduce.UpdatePwd(event.pwd))
            }
            is LoginEvent.OnClickEmailLogin -> {
//                sendSideEffect(LoginSideEffect.NavigateToHome)
            }
            is LoginEvent.OnClickGoogleLogin -> {
                if (currentState.isLoading.not()){
                    updateState(LoginReduce.UpdateLoading(true))
                    sendSideEffect(LoginSideEffect.LaunchGoogleLogin)
                }
            }
            is LoginEvent.OnClickKakaoLogin -> {
                if (currentState.isLoading.not()){
                    updateState(LoginReduce.UpdateLoading(true))
                    sendSideEffect(LoginSideEffect.LaunchKakaoLogin)
                }
            }

            is LoginEvent.ProcessGoogleLogin -> {
                googleLogin(event.account)
            }
            is LoginEvent.ProcessKakaoLogin -> {
                kakaoLogin(event.accessToken)
            }
            is LoginEvent.OnLoginFailure -> {
                if (event.canceled){
                    updateState(LoginReduce.UpdateLoading(false))
                } else {
                    onLoginFailure("로그인에 실패하였습니다.")
                }
            }
        }
    }

    private fun onLoginFailure(message: String){
        updateState(LoginReduce.UpdateLoading(false))
        sendSideEffect(LoginSideEffect.ShowSnackBar(message))
    }

    override fun reduceState(state: LoginState, reduce: LoginReduce): LoginState {
        return when (reduce) {
            is LoginReduce.UpdateEmail -> {
                state.copy(email = reduce.email)
            }

            is LoginReduce.UpdatePwd -> {
                state.copy(password = reduce.pwd)
            }

            is LoginReduce.UpdateLoading -> {
                state.copy(isLoading = reduce.isLoading)
            }
        }
    }
}