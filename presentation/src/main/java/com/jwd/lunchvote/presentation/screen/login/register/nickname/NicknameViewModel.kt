package com.jwd.lunchvote.presentation.screen.login.register.nickname

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.domain.usecase.CreateUserWithEmailAndPassword
import com.jwd.lunchvote.domain.usecase.SignInWithEmailAndPassword
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.screen.login.register.nickname.NicknameContract.NicknameEvent
import com.jwd.lunchvote.presentation.screen.login.register.nickname.NicknameContract.NicknameReduce
import com.jwd.lunchvote.presentation.screen.login.register.nickname.NicknameContract.NicknameSideEffect
import com.jwd.lunchvote.presentation.screen.login.register.nickname.NicknameContract.NicknameState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.inbody.config.error.RouteError
import kr.co.inbody.config.error.UserError
import javax.inject.Inject

@HiltViewModel
class NicknameViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val createUserWithEmailAndPassword: CreateUserWithEmailAndPassword,
  private val signInWithEmailAndPassword: SignInWithEmailAndPassword,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<NicknameState, NicknameEvent, NicknameReduce, NicknameSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): NicknameState {
    return savedState as? NicknameState ?: NicknameState()
  }

  private val email: String =
    savedStateHandle[LunchVoteNavRoute.Nickname.arguments[0].name] ?: throw RouteError.NoArguments
  private val password: String =
    savedStateHandle[LunchVoteNavRoute.Nickname.arguments[1].name] ?: throw RouteError.NoArguments

  override fun handleEvents(event: NicknameEvent) {
    when (event) {
      is NicknameEvent.OnNicknameChange ->  updateState(NicknameReduce.UpdateNickname(event.nickname))
      is NicknameEvent.OnClickNextButton -> launch { signUp() }
    }
  }

  override fun reduceState(state: NicknameState, reduce: NicknameReduce): NicknameState {
    return when (reduce) {
      is NicknameReduce.UpdateNickname -> state.copy(nickname = reduce.nickname)
    }
  }

  override fun handleErrors(error: Throwable) {
    when (error) {
      is FirebaseAuthUserCollisionException -> sendSideEffect(NicknameSideEffect.ShowSnackbar(UiText.StringResource(R.string.nickname_email_collision_error_snackbar)))
      is UserError.DuplicatedName -> sendSideEffect(NicknameSideEffect.ShowSnackbar(UiText.StringResource(R.string.nickname_name_collision_error_snackbar)))
      else -> sendSideEffect(NicknameSideEffect.ShowSnackbar(UiText.ErrorString(error)))
    }
  }

  private suspend fun signUp() {
    val isNameExist = userRepository.checkNameExists(currentState.nickname)
    if (isNameExist) throw UserError.DuplicatedName

    createUserWithEmailAndPassword(email, password)
    val userId = signInWithEmailAndPassword(email, password)
    val nickname = currentState.nickname

    val user = UserUIModel(
      id = userId,
      email = email,
      name = nickname
    )
    userRepository.createUser(user.asDomain())

    sendSideEffect(NicknameSideEffect.ShowSnackbar(UiText.StringResource(R.string.nickname_success_snackbar)))
    sendSideEffect(NicknameSideEffect.NavigateToHome)
  }
}