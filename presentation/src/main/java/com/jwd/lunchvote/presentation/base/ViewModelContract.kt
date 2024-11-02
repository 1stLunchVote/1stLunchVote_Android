package com.jwd.lunchvote.presentation.base

import android.os.Parcelable

sealed interface ViewModelContract {
  /**
   * 화면에 표시되는 상태
   */
  interface State : ViewModelContract {
    fun toParcelable(): Parcelable? = null
  }

  /**
   * 사용자로부터 발생하는 이벤트
   */
  interface Event : ViewModelContract

  /**
   * 상태가 변경되는 시나리오
   *
   * 상태를 immutable하게 관리하기 위함
   */
  interface Reduce : ViewModelContract

  /**
   * 상태에 영향을 주지 않지만, 암시적으로 화면에 영향을 주는 부수 효과
   */
  interface SideEffect : ViewModelContract
}