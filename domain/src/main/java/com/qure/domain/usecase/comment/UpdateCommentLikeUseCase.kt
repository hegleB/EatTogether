package com.qure.domain.usecase.comment

import com.qure.domain.repository.CommentRepository
import javax.inject.Inject

class UpdateCommentLikeUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(commentKey: String, commentLikeList: ArrayList<String>) =
        commentRepository.updateCommentLike(commentKey, commentLikeList)
}