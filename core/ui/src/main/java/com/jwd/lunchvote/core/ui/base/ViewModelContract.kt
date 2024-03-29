package com.jwd.lunchvote.core.ui.base

import android.os.Parcelable

sealed interface ViewModelContract{
    interface State: ViewModelContract {
        // 저장하고 싶은 값이 없으면 구현 안해도 된다.
        fun toParcelable(): Parcelable? = null
    }

    // View에서 일어난 이벤트 -> ViewModel 에서 처리
    interface Event: ViewModelContract

    // 단순한 상태 변화 , Immutable한 상태로 변경시키기 위해
    interface Reduce: ViewModelContract

    // View 에서 처리 (상태 변경 없이 처리 해야 할 것 혹은 외부의 상태 변화)
    interface SideEffect: ViewModelContract

    // Dialog 상태
    interface DialogState: ViewModelContract
}