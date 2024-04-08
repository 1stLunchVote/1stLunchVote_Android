package com.jwd.lunchvote.domain.usecase.template

import com.jwd.lunchvote.domain.repository.TemplateRepository
import javax.inject.Inject

class GetTemplateListUseCase @Inject constructor(
  private val templateRepository: TemplateRepository
) {
  suspend operator fun invoke(userId: String) = templateRepository.getTemplateList(userId)
}