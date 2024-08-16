package com.jwd.lunchvote.data.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.ContactData
import com.jwd.lunchvote.domain.entity.Contact

private object ContactDataMapper : BiMapper<ContactData, Contact> {
  override fun mapToRight(from: ContactData): Contact =
    Contact(
      id = from.id,
      userId = from.userId,
      title = from.title,
      category = from.category.asDomain(),
      content = from.content,
      createdAt = from.createdAt
    )

  override fun mapToLeft(from: Contact): ContactData =
    ContactData(
      id = from.id,
      userId = from.userId,
      title = from.title,
      category = from.category.asData(),
      content = from.content,
      createdAt = from.createdAt
    )
}

private object ContactDataTypeMapper : BiMapper<ContactData.Category, Contact.Category> {
  override fun mapToRight(from: ContactData.Category): Contact.Category =
    when (from) {
      ContactData.Category.ACCOUNT -> Contact.Category.ACCOUNT
      ContactData.Category.BUG -> Contact.Category.BUG
      ContactData.Category.SUGGESTION -> Contact.Category.SUGGESTION
      ContactData.Category.ETC -> Contact.Category.ETC
    }

  override fun mapToLeft(from: Contact.Category): ContactData.Category =
    when (from) {
      Contact.Category.ACCOUNT -> ContactData.Category.ACCOUNT
      Contact.Category.BUG -> ContactData.Category.BUG
      Contact.Category.SUGGESTION -> ContactData.Category.SUGGESTION
      Contact.Category.ETC -> ContactData.Category.ETC
    }
}

internal fun ContactData.asDomain(): Contact =
  ContactDataMapper.mapToRight(this)

internal fun Contact.asData(): ContactData =
  ContactDataMapper.mapToLeft(this)

internal fun ContactData.Category.asDomain(): Contact.Category =
  ContactDataTypeMapper.mapToRight(this)

internal fun Contact.Category.asData(): ContactData.Category =
  ContactDataTypeMapper.mapToLeft(this)