package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.TemplateRepository
import javax.inject.Inject

class GetTemplateUseCase @Inject constructor(
  private val templateRepository: TemplateRepository
) {
  suspend operator fun invoke(id: String) = templateRepository.getTemplate(id)
}