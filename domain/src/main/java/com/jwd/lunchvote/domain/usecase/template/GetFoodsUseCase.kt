package com.jwd.lunchvote.domain.usecase.template

import com.jwd.lunchvote.domain.repository.TemplateRepository
import javax.inject.Inject

class GetFoodsUseCase @Inject constructor(
  private val templateRepository: TemplateRepository
) {
  suspend operator fun invoke() = templateRepository.getFoods()
}