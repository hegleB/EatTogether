package com.qure.domain.usecase.comment

import com.qure.domain.model.Comments
import com.qure.domain.repository.CommentRepository
import javax.inject.Inject

class UpdateRecommentLikeUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(recomments : Comments, count : ArrayList<String>) = commentRepository.updateRecommentLike(recomments, count)
}