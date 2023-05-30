package com.jwd.lunchvote.ui.login

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.functions.FirebaseFunctions
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.ui.login.LoginContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
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
                sendSideEffect(LoginSideEffect.ShowSnackBar(task.exception?.message ?: "Unknown error"))
            }
        }
    }

    private fun kakaoLogin(accessToken: String){
        val data = JSONObject()
        data.put("accessToken", accessToken)
        FirebaseFunctions.getInstance("asia-northeast3").getHttpsCallable("kakaoToken")
            .call(data)
            .addOnSuccessListener {
                val token = it.data as String
                Timber.e(it.data as String)
                auth.signInWithCustomToken(token)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            sendSideEffect(LoginSideEffect.NavigateToHome)
                        } else {
                            sendSideEffect(LoginSideEffect.ShowSnackBar(task.exception?.message ?: "Unknown error"))
                        }
                    }
            }
            .addOnFailureListener {
                Timber.e(it.message)
                sendSideEffect(LoginSideEffect.ShowSnackBar("로그인에 실패하였습니다."))
            }
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
                sendSideEffect(LoginSideEffect.LaunchGoogleLogin)
            }
            is LoginEvent.OnClickKakaoLogin -> {
                sendSideEffect(LoginSideEffect.LaunchKakaoLogin)
            }

            is LoginEvent.ProcessGoogleLogin -> {
                googleLogin(event.account)
            }
            is LoginEvent.ProcessKakaoLogin -> {
                kakaoLogin(event.accessToken)
            }
            is LoginEvent.OnLoginFailure -> {
                sendSideEffect(LoginSideEffect.ShowSnackBar("로그인에 실패하였습니다."))
            }

        }
    }

    override fun reduceState(state: LoginState, reduce: LoginReduce): LoginState {
        return when (reduce) {
            is LoginReduce.UpdateEmail -> {
                state.copy(email = reduce.email)
            }

            is LoginReduce.UpdatePwd -> {
                state.copy(password = reduce.pwd)
            }
        }
    }
}