package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.domain.repository.TemplateRepository
import javax.inject.Inject

class AddTemplateUseCase @Inject constructor(
  private val templateRepository: TemplateRepository
) {
  suspend operator fun invoke(template: Template) = templateRepository.addTemplate(template)
}