package com.qure.domain.usecase

import com.qure.domain.model.Comments
import com.qure.domain.repository.CommentRepository
import javax.inject.Inject

class CommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend fun checkComment(commentKey: String) = commentRepository.checkComment(commentKey)
    suspend fun checkReComment(recomment: Comments) = commentRepository.checkReComment(recomment)
    suspend fun getComments(postKey: String) = commentRepository.getComments(postKey)
    suspend fun getReComments(comments: Comments) = commentRepository.getReComments(comments)
    suspend fun setComments(comments: Comments) = commentRepository.setComments(comments)
    suspend fun setReComments(recomments: Comments) = commentRepository.setReComments(recomments)
    suspend fun updateCommentLike(commentKey: String, commentLikeList: ArrayList<String>) =
        commentRepository.updateCommentLike(commentKey, commentLikeList)

    suspend fun updateCommentsCount(postKey: String, count: String) =
        commentRepository.updateCommentsCount(postKey, count)

    suspend fun updateRecommentLike(recomments: Comments, count: ArrayList<String>) =
        commentRepository.updateRecommentLike(recomments, count)
}