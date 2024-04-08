package com.jwd.lunchvote.domain.usecase.template

import com.jwd.lunchvote.domain.repository.TemplateRepository
import javax.inject.Inject

class GetFoodListUseCase @Inject constructor(
  private val templateRepository: TemplateRepository
) {
  suspend operator fun invoke() = templateRepository.getFoodList()
}