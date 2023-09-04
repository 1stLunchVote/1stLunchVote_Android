package com.jwd.lunchvote.ui.template.edit_template

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract.DialogState
import com.jwd.lunchvote.core.ui.base.ViewModelContract.Event
import com.jwd.lunchvote.core.ui.base.ViewModelContract.Reduce
import com.jwd.lunchvote.core.ui.base.ViewModelContract.SideEffect
import com.jwd.lunchvote.core.ui.base.ViewModelContract.State
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.model.FoodUIModel
import com.jwd.lunchvote.model.TemplateUIModel
import kotlinx.parcelize.Parcelize

class EditTemplateContract {
  @Parcelize
  data class EditTemplateState(
    val loading: Boolean = false,
    val template: TemplateUIModel = TemplateUIModel(Template()),
    val foodList: List<FoodUIModel> = emptyList(),
    val likeList: List<FoodUIModel> = emptyList(),
    val dislikeList: List<FoodUIModel> = emptyList(),
    val searchKeyword: String = ""
  ): State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface EditTemplateEvent: Event {
    data class StartInitialize(val templateId: String): EditTemplateEvent
    data object OnClickBackButton: EditTemplateEvent
    data class SetSearchKeyword(val searchKeyword: String): EditTemplateEvent
    data class OnClickFood(val food: FoodUIModel): EditTemplateEvent
    data object OnClickSaveButton: EditTemplateEvent
    data object OnClickDeleteButton: EditTemplateEvent
    data object OnClickDialogDismiss: EditTemplateEvent
  }

  sealed interface EditTemplateReduce : Reduce {
    data class UpdateLoading(val loading: Boolean): EditTemplateReduce
    data class Initialize(val state: EditTemplateState): EditTemplateReduce
    data class UpdateSearchKeyword(val searchKeyword: String): EditTemplateReduce
    data class UpdateFoodStatus(val food: FoodUIModel): EditTemplateReduce
  }

  sealed interface EditTemplateSideEffect: SideEffect {
    data class PopBackStack(val message: String = ""): EditTemplateSideEffect
    data class ShowSnackBar(val message: String) : EditTemplateSideEffect
  }

  sealed interface EditTemplateDialogState: DialogState {
    data class DeleteTemplateConfirm(val onClickConfirm: () -> Unit): EditTemplateDialogState
    data class EditTemplateConfirm(val onClickConfirm: () -> Unit): EditTemplateDialogState
  }
}