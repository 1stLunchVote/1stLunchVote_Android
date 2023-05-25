package com.jwd.lunchvote.ui.login

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.ui.login.LoginContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): BaseStateViewModel<LoginState, LoginEvent, LoginReduce, LoginSideEffect>(savedStateHandle){
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    override fun createInitialState(savedState: Parcelable?): LoginState {
        return savedState as? LoginState ?: LoginState()
    }

    private fun googleLogin(account: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if(task.isSuccessful){
                sendSideEffect(LoginSideEffect.NavigateToHome)
            } else {
                sendSideEffect(LoginSideEffect.ShowSnackbar(task.exception?.message ?: "Unknown error"))
            }
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
            is LoginEvent.ProcessGoogleLogin -> {
                googleLogin(event.account)
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