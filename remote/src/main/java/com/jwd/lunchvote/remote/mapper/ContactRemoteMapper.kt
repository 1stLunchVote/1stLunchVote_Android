package com.jwd.lunchvote.remote.mapper

import com.jwd.lunchvote.core.common.mapper.BiMapper
import com.jwd.lunchvote.data.model.ContactData
import com.jwd.lunchvote.remote.model.ContactRemote
import com.jwd.lunchvote.remote.util.toLong
import com.jwd.lunchvote.remote.util.toTimestamp
import kr.co.inbody.config.error.ContactError

private object ContactRemoteMapper : BiMapper<ContactRemote, ContactData> {
  override fun mapToRight(from: ContactRemote): ContactData =
    ContactData(
      id = from.id,
      userId = from.userId,
      title = from.title,
      category = from.category.asContactDataCategory(),
      content = from.content,
      createdAt = from.createdAt.toLong()
    )

  override fun mapToLeft(from: ContactData): ContactRemote =
    ContactRemote(
      id = from.id,
      userId = from.userId,
      title = from.title,
      category = from.category.asRemote(),
      content = from.content,
      createdAt = from.createdAt.toTimestamp()
    )
}

private object ContactRemoteCategoryMapper : BiMapper<String, ContactData.Category> {
  override fun mapToRight(from: String): ContactData.Category =
    when (from) {
      ContactRemote.CATEGORY_ACCOUNT -> ContactData.Category.ACCOUNT
      ContactRemote.CATEGORY_BUG -> ContactData.Category.BUG
      ContactRemote.CATEGORY_SUGGESTION -> ContactData.Category.SUGGESTION
      ContactRemote.CATEGORY_ETC -> ContactData.Category.ETC
      else -> throw ContactError.ContactCategoryError
    }

  override fun mapToLeft(from: ContactData.Category): String =
    when (from) {
      ContactData.Category.ACCOUNT -> ContactRemote.CATEGORY_ACCOUNT
      ContactData.Category.BUG -> ContactRemote.CATEGORY_BUG
      ContactData.Category.SUGGESTION -> ContactRemote.CATEGORY_SUGGESTION
      ContactData.Category.ETC -> ContactRemote.CATEGORY_ETC
    }
}

internal fun ContactRemote.asData(): ContactData =
  ContactRemoteMapper.mapToRight(this)

internal fun ContactData.asRemote(): ContactRemote =
  ContactRemoteMapper.mapToLeft(this)

internal fun String.asContactDataCategory(): ContactData.Category =
  ContactRemoteCategoryMapper.mapToRight(this)

internal fun ContactData.Category.asRemote(): String =
  ContactRemoteCategoryMapper.mapToLeft(this)