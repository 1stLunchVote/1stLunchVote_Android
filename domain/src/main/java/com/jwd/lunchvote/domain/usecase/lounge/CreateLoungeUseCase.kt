package com.jwd.lunchvote.domain.usecase.lounge

import com.jwd.lunchvote.domain.repository.LoungeRepository
import javax.inject.Inject

class CreateLoungeUseCase @Inject constructor(
    private val repository: LoungeRepository
){
    operator fun invoke() = repository.createLounge()
}