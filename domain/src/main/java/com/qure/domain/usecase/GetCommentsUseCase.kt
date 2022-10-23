package com.qure.domain.usecase

import com.qure.domain.repository.CommentRepository
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(postKey: String) = commentRepository.getComments(postKey)
}