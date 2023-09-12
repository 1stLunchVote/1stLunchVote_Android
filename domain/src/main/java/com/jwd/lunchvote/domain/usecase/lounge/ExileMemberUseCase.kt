package com.jwd.lunchvote.domain.usecase.lounge

import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExileMemberUseCase @Inject constructor(
    private val repository: LoungeRepository
){
    suspend operator fun invoke(memberId: String, loungeId: String) = repository.exileMember(memberId, loungeId)
}