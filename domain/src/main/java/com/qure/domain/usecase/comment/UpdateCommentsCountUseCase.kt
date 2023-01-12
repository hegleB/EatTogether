package com.qure.domain.usecase.comment

import com.qure.domain.repository.CommentRepository
import javax.inject.Inject

class UpdateCommentsCountUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(postKey: String, count: String) =
        commentRepository.updateCommentsCount(postKey, count)
}