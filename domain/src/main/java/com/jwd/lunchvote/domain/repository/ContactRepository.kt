package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.Contact
import com.jwd.lunchvote.domain.entity.ContactReply

interface ContactRepository {

  suspend fun addContact(contact: Contact): String
  suspend fun getContactById(id: String): Contact
  suspend fun getContactList(userId: String): List<Contact>
  suspend fun getContactReply(contactId: String): ContactReply?
  suspend fun deleteContract(id: String)
}