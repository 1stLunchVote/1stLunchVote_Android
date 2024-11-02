package com.jwd.lunchvote.presentation.mapper

import com.jwd.lunchvote.mapper.BiMapper
import com.jwd.lunchvote.domain.entity.Contact
import com.jwd.lunchvote.presentation.model.ContactUIModel
import com.jwd.lunchvote.presentation.util.toLong
import com.jwd.lunchvote.presentation.util.toZonedDateTime

private object ContactUIMapper : BiMapper<ContactUIModel, Contact> {
  override fun mapToRight(from: ContactUIModel): Contact =
    Contact(
      id = from.id,
      userId = from.userId,
      title = from.title,
      category = from.category.asDomain(),
      content = from.content,
      createdAt = from.createdAt.toLong(),
      deletedAt = from.deletedAt?.toLong()
    )

  override fun mapToLeft(from: Contact): ContactUIModel =
    ContactUIModel(
      id = from.id,
      userId = from.userId,
      title = from.title,
      category = from.category.asData(),
      content = from.content,
      createdAt = from.createdAt.toZonedDateTime(),
      deletedAt = from.deletedAt?.toZonedDateTime()
    )
}

private object ContactUICategoryMapper : BiMapper<ContactUIModel.Category, Contact.Category> {
  override fun mapToRight(from: ContactUIModel.Category): Contact.Category =
    when (from) {
      ContactUIModel.Category.ACCOUNT -> Contact.Category.ACCOUNT
      ContactUIModel.Category.BUG -> Contact.Category.BUG
      ContactUIModel.Category.SUGGESTION -> Contact.Category.SUGGESTION
      ContactUIModel.Category.ETC -> Contact.Category.ETC
    }

  override fun mapToLeft(from: Contact.Category): ContactUIModel.Category =
    when (from) {
      Contact.Category.ACCOUNT -> ContactUIModel.Category.ACCOUNT
      Contact.Category.BUG -> ContactUIModel.Category.BUG
      Contact.Category.SUGGESTION -> ContactUIModel.Category.SUGGESTION
      Contact.Category.ETC -> ContactUIModel.Category.ETC
    }
}

internal fun ContactUIModel.asDomain(): Contact =
  ContactUIMapper.mapToRight(this)

internal fun Contact.asUI(): ContactUIModel =
  ContactUIMapper.mapToLeft(this)

internal fun ContactUIModel.Category.asDomain(): Contact.Category =
  ContactUICategoryMapper.mapToRight(this)

internal fun Contact.Category.asData(): ContactUIModel.Category =
  ContactUICategoryMapper.mapToLeft(this)