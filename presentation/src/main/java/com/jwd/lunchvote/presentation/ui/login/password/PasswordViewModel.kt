package com.jwd.lunchvote.presentation.ui.login.password

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.common.error.LoginError
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.GetEmailUseCase
import com.jwd.lunchvote.presentation.ui.login.password.PasswordContract.PasswordEvent
import com.jwd.lunchvote.presentation.ui.login.password.PasswordContract.PasswordReduce
import com.jwd.lunchvote.presentation.ui.login.password.PasswordContract.PasswordSideEffect
import com.jwd.lunchvote.presentation.ui.login.password.PasswordContract.PasswordState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
  private val getEmailUseCase: GetEmailUseCase,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<PasswordState, PasswordEvent, PasswordReduce, PasswordSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): PasswordState {
    return savedState as? PasswordState ?: PasswordState()
  }

  override fun handleEvents(event: PasswordEvent) {
    when (event) {
      is PasswordEvent.OnPasswordChanged -> updateState(PasswordReduce.UpdatePassword(event.password))
      is PasswordEvent.OnPasswordConfirmChanged -> updateState(PasswordReduce.UpdatePasswordConfirm(event.passwordConfirm))
      is PasswordEvent.OnClickNextButton -> launch {
        val email = getEmailUseCase() ?: throw LoginError.NoEmail
        sendSideEffect(PasswordSideEffect.NavigateToNickname(email, currentState.password))
      }
    }
  }

  override fun reduceState(state: PasswordState, reduce: PasswordReduce): PasswordState {
    return when (reduce) {
      is PasswordReduce.UpdatePassword -> state.copy(password = reduce.password)
      is PasswordReduce.UpdatePasswordConfirm -> state.copy(passwordConfirm = reduce.passwordConfirm)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(PasswordSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }
}