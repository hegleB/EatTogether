package com.qure.domain.usecase

import com.qure.domain.model.Comments
import com.qure.domain.repository.CommentRepository
import javax.inject.Inject

class SetCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(comments: Comments) = commentRepository.setComments(comments)
}