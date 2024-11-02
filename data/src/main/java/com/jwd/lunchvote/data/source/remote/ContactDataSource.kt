package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.ContactData
import com.jwd.lunchvote.data.model.ContactReplyData

interface ContactDataSource {

  suspend fun addContact(contact: ContactData): String
  suspend fun getContactById(id: String): ContactData
  suspend fun getContactList(userId: String): List<ContactData>
  suspend fun getContactReply(contactId: String): ContactReplyData?
  suspend fun deleteContract(id: String)
}