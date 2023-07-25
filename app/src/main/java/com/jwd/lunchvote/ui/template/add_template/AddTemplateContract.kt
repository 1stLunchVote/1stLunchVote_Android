package com.jwd.lunchvote.ui.template.add_template

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.model.TemplateUIModel
import kotlinx.parcelize.Parcelize

class AddTemplateContract {
  @Parcelize
  data class AddTemplateState(
    val loading: Boolean = false,
    val template: TemplateUIModel = TemplateUIModel("", "", emptyList(), emptyList())
  ): ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface AddTemplateEvent: ViewModelContract.Event {
    object StartInitialize: AddTemplateEvent
    object OnClickBackButton: AddTemplateEvent
  }

  sealed interface AddTemplateReduce : ViewModelContract.Reduce {
    data class UpdateLoading(val loading: Boolean): AddTemplateReduce
    data class Initialize(val state: AddTemplateState): AddTemplateReduce
  }

  sealed interface AddTemplateSideEffect: ViewModelContract.SideEffect {
    data class PopBackStack(val message: String = ""): AddTemplateSideEffect
    data class ShowSnackBar(val message: String) : AddTemplateSideEffect
  }
}