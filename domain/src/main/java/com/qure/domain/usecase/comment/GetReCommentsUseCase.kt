package com.qure.domain.usecase.comment

import com.qure.domain.model.Comments
import com.qure.domain.repository.CommentRepository
import javax.inject.Inject

class GetReCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(comments : Comments) = commentRepository.getReComments(comments)
}