package com.jwd.lunchvote.presentation.ui.login.register

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.ui.login.register.RegisterEmailContract.RegisterEmailDialogState
import com.jwd.lunchvote.presentation.ui.login.register.RegisterEmailContract.RegisterEmailEvent
import com.jwd.lunchvote.presentation.ui.login.register.RegisterEmailContract.RegisterEmailReduce
import com.jwd.lunchvote.presentation.ui.login.register.RegisterEmailContract.RegisterEmailSideEffect
import com.jwd.lunchvote.presentation.ui.login.register.RegisterEmailContract.RegisterEmailState
import javax.inject.Inject

class RegisterEmailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): BaseStateViewModel<RegisterEmailState, RegisterEmailEvent, RegisterEmailReduce, RegisterEmailSideEffect, RegisterEmailDialogState>(savedStateHandle) {
    override fun createInitialState(savedState: Parcelable?): RegisterEmailState {
        return savedState as? RegisterEmailState ?: RegisterEmailState()
    }

    private fun verifyEmail(){
        if (checkEmailType()){
            // Todo : 이메일 db에 존재하는지 체크, 인증이 마무리 안된 유저라면 인증 마무리 하라고 표시
        } else {
            sendSideEffect(RegisterEmailSideEffect.ShowSnackBar("이메일 형식이 올바르지 않습니다."))
        }
    }

    override fun handleEvents(event: RegisterEmailEvent) {
        when (event) {
            is RegisterEmailEvent.SetEmail -> {
                updateState(RegisterEmailReduce.UpdateEmail(event.email))
            }
            is RegisterEmailEvent.OnClickConfirm -> {
                verifyEmail()
            }
        }
    }

    override fun reduceState(state: RegisterEmailState, reduce: RegisterEmailReduce, ): RegisterEmailState {
        return when (reduce) {
            is RegisterEmailReduce.UpdateEmail -> {
                state.copy(email = reduce.email)
            }
        }
    }

    private fun checkEmailType() : Boolean {
        val regEmail = Regex("^([0-9a-zA-Z_\\.-]+)@([0-9a-zA-Z_-]+)(\\.[0-9a-zA-Z_-]+){1,2}$")
        return regEmail.matches(currentState.email)
    }
}