package com.jwd.lunchvote.presentation.screen.login.register.password

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.domain.usecase.GetEmail
import com.jwd.lunchvote.domain.usecase.SetEmail
import com.jwd.lunchvote.presentation.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.screen.login.register.password.PasswordContract.PasswordEvent
import com.jwd.lunchvote.presentation.screen.login.register.password.PasswordContract.PasswordReduce
import com.jwd.lunchvote.presentation.screen.login.register.password.PasswordContract.PasswordSideEffect
import com.jwd.lunchvote.presentation.screen.login.register.password.PasswordContract.PasswordState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.inbody.config.error.LoginError
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
  private val getEmail: GetEmail,
  private val setEmail: SetEmail,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<PasswordState, PasswordEvent, PasswordReduce, PasswordSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): PasswordState {
    return savedState as? PasswordState ?: PasswordState()
  }

  override fun handleEvents(event: PasswordEvent) {
    when (event) {
      is PasswordEvent.ScreenInitialize -> initialize()

      is PasswordEvent.OnPasswordChange -> updateState(PasswordReduce.UpdatePassword(event.password))
      is PasswordEvent.OnPasswordConfirmChange -> updateState(PasswordReduce.UpdatePasswordConfirm(event.passwordConfirm))
      is PasswordEvent.OnClickNextButton -> sendSideEffect(PasswordSideEffect.NavigateToNickname(currentState.email, currentState.password))
    }
  }

  override fun reduceState(state: PasswordState, reduce: PasswordReduce): PasswordState {
    return when (reduce) {
      is PasswordReduce.UpdateEmail -> state.copy(email = reduce.email)
      is PasswordReduce.UpdatePassword -> state.copy(password = reduce.password)
      is PasswordReduce.UpdatePasswordConfirm -> state.copy(passwordConfirm = reduce.passwordConfirm)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(PasswordSideEffect.ShowSnackbar(UiText.ErrorString(error)))
    when (error) {
      is LoginError.NoEmail -> sendSideEffect(PasswordSideEffect.NavigateToLogin)
    }
  }

  private fun initialize() {
    val email = getEmail() ?: throw LoginError.NoEmail
    setEmail(null)
    updateState(PasswordReduce.UpdateEmail(email))
  }
}