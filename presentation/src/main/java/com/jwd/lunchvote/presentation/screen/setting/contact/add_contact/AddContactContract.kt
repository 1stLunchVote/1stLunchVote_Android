package com.jwd.lunchvote.presentation.screen.setting.contact.add_contact

import android.os.Parcelable
import com.jwd.lunchvote.presentation.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.ContactUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class AddContactContract {
  @Parcelize
  data class AddContactState(
    val title: String = "",
    val category: ContactUIModel.Category? = null,
    val content: String = ""
  ): ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface AddContactEvent: ViewModelContract.Event {
    data object OnClickBackButton: AddContactEvent
    data class OnTitleChange(val title: String): AddContactEvent
    data class OnCategoryChange(val category: ContactUIModel.Category): AddContactEvent
    data class OnContentChange(val content: String): AddContactEvent
    data object OnClickSubmitButton: AddContactEvent
  }

  sealed interface AddContactReduce : ViewModelContract.Reduce {
    data class UpdateTitle(val title: String) : AddContactReduce
    data class UpdateCategory(val category: ContactUIModel.Category) : AddContactReduce
    data class UpdateContent(val content: String) : AddContactReduce
  }

  sealed interface AddContactSideEffect: ViewModelContract.SideEffect {
    data object PopBackStack : AddContactSideEffect
    data class NavigateToContact(val contactId: String) : AddContactSideEffect
    data class ShowSnackbar(val message: UiText) : AddContactSideEffect
  }
}