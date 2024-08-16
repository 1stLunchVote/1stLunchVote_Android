package com.jwd.lunchvote.remote.source

import com.google.firebase.firestore.FirebaseFirestore
import com.jwd.lunchvote.data.model.ContactData
import com.jwd.lunchvote.data.model.ContactReplyData
import com.jwd.lunchvote.data.source.ContactDataSource
import com.jwd.lunchvote.remote.mapper.asData
import com.jwd.lunchvote.remote.mapper.asRemote
import com.jwd.lunchvote.remote.model.ContactRemote
import com.jwd.lunchvote.remote.model.ContactReplyRemote
import com.jwd.lunchvote.remote.util.deleteDocument
import com.jwd.lunchvote.remote.util.whereNotDeleted
import kotlinx.coroutines.tasks.await
import kr.co.inbody.config.error.ContactError
import javax.inject.Inject

class ContactDataSourceImpl @Inject constructor(
  private val fireStore: FirebaseFirestore
): ContactDataSource {

  override suspend fun addContact(
    contact: ContactData
  ): String =
    fireStore
      .collection(COLLECTION_CONTACT)
      .add(contact.asRemote())
      .await()
      .id

  override suspend fun getContactById(
    id: String
  ): ContactData =
    fireStore
      .collection(COLLECTION_CONTACT)
      .document(id)
      .get()
      .await()
      .toObject(ContactRemote::class.java)
      .let { contact ->
        if (contact == null) throw ContactError.NoContact
        else if (contact.deletedAt != null) throw ContactError.DeletedContact
        else contact.asData(id)
      }

  override suspend fun getContactList(
    userId: String
  ): List<ContactData> =
    fireStore
      .collection(COLLECTION_CONTACT)
      .whereNotDeleted()
      .whereEqualTo(COLUMN_CONTACT_USER_ID, userId)
      .get()
      .await()
      .documents
      .mapNotNull {
        it.toObject(ContactRemote::class.java)
          ?.asData(it.id)
      }

  override suspend fun getContactReply(
    contactId: String
  ): ContactReplyData? =
    fireStore
      .collection(COLLECTION_CONTACT_REPLY)
      .whereEqualTo(COLUMN_CONTACT_REPLY_CONTACT_ID, contactId)
      .get()
      .await()
      .documents
      .firstNotNullOfOrNull {
        it.toObject(ContactReplyRemote::class.java)
          ?.asData(it.id)
      }

  override suspend fun deleteContract(
    id: String
  ) {
    fireStore
      .collection(COLLECTION_CONTACT)
      .document(id)
      .deleteDocument()
      .await()
  }

  companion object {
    private const val COLLECTION_CONTACT = "Contact"

    private const val COLUMN_CONTACT_USER_ID = "userId"
    private const val COLUMN_CONTACT_TITLE = "title"
    private const val COLUMN_CONTACT_CATEGORY = "category"
    private const val COLUMN_CONTACT_CONTENT = "content"
    private const val COLUMN_CONTACT_CREATED_AT = "createdAt"

    private const val COLLECTION_CONTACT_REPLY = "ContactReply"

    private const val COLUMN_CONTACT_REPLY_CONTACT_ID = "contactId"
    private const val COLUMN_CONTACT_REPLY_TITLE = "title"
    private const val COLUMN_CONTACT_REPLY_CONTENT = "content"
    private const val COLUMN_CONTACT_REPLY_CREATED_AT = "createdAt"
  }
}