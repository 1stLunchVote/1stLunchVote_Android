package com.jwd.lunchvote.di

import com.jwd.lunchvote.data.repository.ChatRepositoryImpl
import com.jwd.lunchvote.data.repository.FirstVoteRepositoryImpl
import com.jwd.lunchvote.data.repository.FoodRepositoryImpl
import com.jwd.lunchvote.data.repository.LoginRepositoryImpl
import com.jwd.lunchvote.data.repository.LoungeRepositoryImpl
import com.jwd.lunchvote.data.repository.MemberRepositoryImpl
import com.jwd.lunchvote.data.repository.PreferenceRepositoryImpl
import com.jwd.lunchvote.data.repository.SecondVoteRepositoryImpl
import com.jwd.lunchvote.data.repository.StorageRepositoryImpl
import com.jwd.lunchvote.data.repository.TemplateRepositoryImpl
import com.jwd.lunchvote.data.repository.UserRepositoryImpl
import com.jwd.lunchvote.data.repository.VoteResultRepositoryImpl
import com.jwd.lunchvote.domain.repository.ChatRepository
import com.jwd.lunchvote.domain.repository.FirstVoteRepository
import com.jwd.lunchvote.domain.repository.FoodRepository
import com.jwd.lunchvote.domain.repository.LoginRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import com.jwd.lunchvote.domain.repository.PreferenceRepository
import com.jwd.lunchvote.domain.repository.SecondVoteRepository
import com.jwd.lunchvote.domain.repository.StorageRepository
import com.jwd.lunchvote.domain.repository.TemplateRepository
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.domain.repository.VoteResultRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

  @Binds
  @Singleton
  abstract fun bindsChatRepository(
    repository: ChatRepositoryImpl
  ): ChatRepository

  @Binds
  @Singleton
  abstract fun bindsFirstVoteRepository(
    repository: FirstVoteRepositoryImpl
  ): FirstVoteRepository

  @Binds
  @Singleton
  abstract fun bindsFoodRepository(
    repository: FoodRepositoryImpl
  ): FoodRepository

  @Binds
  @Singleton
  abstract fun bindsLoginRepository(
    repository: LoginRepositoryImpl
  ): LoginRepository

  @Binds
  @Singleton
  abstract fun bindsLoungeRepository(
    repository: LoungeRepositoryImpl
  ): LoungeRepository

  @Binds
  @Singleton
  abstract fun bindsMemberRepository(
    repository: MemberRepositoryImpl
  ): MemberRepository

  @Binds
  @Singleton
  abstract fun bindsPreferenceRepository(
    repository: PreferenceRepositoryImpl
  ): PreferenceRepository

  @Binds
  @Singleton
  abstract fun bindsSecondVoteRepository(
    repository: SecondVoteRepositoryImpl
  ): SecondVoteRepository

  @Binds
  @Singleton
  abstract fun bindsStorageRepository(
    repository: StorageRepositoryImpl
  ): StorageRepository

  @Binds
  @Singleton
  abstract fun bindsTemplateRepository(
    repository: TemplateRepositoryImpl
  ): TemplateRepository

  @Binds
  @Singleton
  abstract fun bindsUserRepository(
    repository: UserRepositoryImpl
  ): UserRepository

  @Binds
  @Singleton
  abstract fun bindsVoteResultRepository(
    repository: VoteResultRepositoryImpl
  ): VoteResultRepository
}