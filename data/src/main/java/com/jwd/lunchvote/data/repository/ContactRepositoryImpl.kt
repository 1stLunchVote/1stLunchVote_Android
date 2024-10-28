package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asData
import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.source.ContactDataSource
import com.jwd.lunchvote.domain.entity.Contact
import com.jwd.lunchvote.domain.entity.ContactReply
import com.jwd.lunchvote.domain.repository.ContactRepository
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
  private val contactDataSource: ContactDataSource
): ContactRepository {

  override suspend fun addContact(contact: Contact): String =
    contactDataSource.addContact(contact.asData())

  override suspend fun getContactById(id: String): Contact =
    contactDataSource.getContactById(id).asDomain()

  override suspend fun getContactList(userId: String): List<Contact> =
    contactDataSource.getContactList(userId).map { it.asDomain() }

  override suspend fun getContactReply(contactId: String): ContactReply? =
    contactDataSource.getContactReply(contactId)?.asDomain()

  override suspend fun deleteContract(id: String) {
    contactDataSource.deleteContract(id)
  }
}