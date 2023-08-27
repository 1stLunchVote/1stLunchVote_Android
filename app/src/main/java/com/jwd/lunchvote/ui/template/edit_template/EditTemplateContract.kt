package com.jwd.lunchvote.ui.template.edit_template

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.model.TemplateUIModel
import com.jwd.lunchvote.ui.home.HomeContract
import kotlinx.parcelize.Parcelize

class EditTemplateContract {
  @Parcelize
  data class EditTemplateState(
    val loading: Boolean = false,
    val template: TemplateUIModel = TemplateUIModel(Template())
  ): ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface EditTemplateEvent: ViewModelContract.Event {
    data class StartInitialize(val templateId: String): EditTemplateEvent
    data object OnClickBackButton: EditTemplateEvent
  }

  sealed interface EditTemplateReduce : ViewModelContract.Reduce {
    data class UpdateLoading(val loading: Boolean): EditTemplateReduce
    data class Initialize(val state: EditTemplateState): EditTemplateReduce
  }

  sealed interface EditTemplateSideEffect: ViewModelContract.SideEffect {
    data class PopBackStack(val message: String = ""): EditTemplateSideEffect
    data class ShowSnackBar(val message: String) : EditTemplateSideEffect
  }
}