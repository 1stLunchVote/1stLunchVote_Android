package com.jwd.lunchvote.di

import com.jwd.lunchvote.data.source.ContactDataSource
import com.jwd.lunchvote.data.source.local.PreferenceDataSource
import com.jwd.lunchvote.data.source.remote.BallotDataSource
import com.jwd.lunchvote.data.source.remote.ChatDataSource
import com.jwd.lunchvote.data.source.remote.FoodDataSource
import com.jwd.lunchvote.data.source.remote.FriendDataSource
import com.jwd.lunchvote.data.source.remote.LoginDataSource
import com.jwd.lunchvote.data.source.remote.LoungeDataSource
import com.jwd.lunchvote.data.source.remote.MemberDataSource
import com.jwd.lunchvote.data.source.remote.StorageDataSource
import com.jwd.lunchvote.data.source.remote.TemplateDataSource
import com.jwd.lunchvote.data.source.remote.UserDataSource
import com.jwd.lunchvote.data.source.remote.UserStatusDataSource
import com.jwd.lunchvote.data.source.remote.VoteResultDataSource
import com.jwd.lunchvote.local.source.PreferenceDataSourceImpl
import com.jwd.lunchvote.remote.source.BallotDataSourceImpl
import com.jwd.lunchvote.remote.source.ChatDataSourceImpl
import com.jwd.lunchvote.remote.source.ContactDataSourceImpl
import com.jwd.lunchvote.remote.source.FoodDataSourceImpl
import com.jwd.lunchvote.remote.source.FriendDataSourceImpl
import com.jwd.lunchvote.remote.source.LoginDataSourceImpl
import com.jwd.lunchvote.remote.source.LoungeDataSourceImpl
import com.jwd.lunchvote.remote.source.MemberDataSourceImpl
import com.jwd.lunchvote.remote.source.StorageDataSourceImpl
import com.jwd.lunchvote.remote.source.TemplateDataSourceImpl
import com.jwd.lunchvote.remote.source.UserDataSourceImpl
import com.jwd.lunchvote.remote.source.UserStatusDataSourceImpl
import com.jwd.lunchvote.remote.source.VoteResultDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataSourceModule {

  @Binds
  @Singleton
  abstract fun bindsBallotDataSource(
    source: BallotDataSourceImpl
  ): BallotDataSource

  @Binds
  @Singleton
  abstract fun bindsChatDataSource(
    source: ChatDataSourceImpl
  ): ChatDataSource

  @Binds
  @Singleton
  abstract fun bindsContactDataSource(
    source: ContactDataSourceImpl
  ): ContactDataSource

  @Binds
  @Singleton
  abstract fun bindsFoodDataSource(
    source: FoodDataSourceImpl
  ): FoodDataSource

  @Binds
  @Singleton
  abstract fun bindsFriendDataSource(
    source: FriendDataSourceImpl
  ): FriendDataSource

  @Binds
  @Singleton
  abstract fun bindsLoginDataSource(
    source: LoginDataSourceImpl
  ): LoginDataSource

  @Binds
  @Singleton
  abstract fun bindsLoungeDataSource(
    source: LoungeDataSourceImpl
  ): LoungeDataSource

  @Binds
  @Singleton
  abstract fun bindsMemberDataSource(
    source: MemberDataSourceImpl
  ): MemberDataSource

  @Binds
  @Singleton
  abstract fun bindsStorageDataSource(
    storageDataSourceImpl: StorageDataSourceImpl
  ): StorageDataSource

  @Binds
  @Singleton
  abstract fun bindsTemplateDataSource(
    source: TemplateDataSourceImpl
  ): TemplateDataSource

  @Binds
  @Singleton
  abstract fun bindsUserDataSource(
    source: UserDataSourceImpl
  ): UserDataSource

  @Binds
  @Singleton
  abstract fun bindsUserStatusDataSource(
    source: UserStatusDataSourceImpl
  ): UserStatusDataSource

  @Binds
  @Singleton
  abstract fun bindsPreferenceDataSource(
    source: PreferenceDataSourceImpl
  ): PreferenceDataSource

  @Binds
  @Singleton
  abstract fun bindsVoteResultDataSource(
    source: VoteResultDataSourceImpl
  ): VoteResultDataSource
}














