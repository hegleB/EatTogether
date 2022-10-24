package com.qure.domain.usecase.comment

import com.qure.domain.model.Comments
import com.qure.domain.repository.CommentRepository
import com.qure.domain.repository.PostRepository
import javax.inject.Inject

class SetReCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(recomments : Comments) = commentRepository.setReComments(recomments)
}