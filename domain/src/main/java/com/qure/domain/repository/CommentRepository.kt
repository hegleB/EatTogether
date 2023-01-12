package com.qure.domain.repository

import com.qure.domain.model.Comments
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

typealias CommentsResource = Resource<List<Comments>, String>
typealias AddComments = Resource<Boolean, String>
typealias AddReComments = Resource<Boolean, String>
typealias ReCommentsResource = Resource<List<Comments>, String>
typealias CheckComments = Resource<Comments, String>
typealias CheckReComments = Resource<Comments, String>
typealias UpdateCommentsLike = Resource<Boolean, String>
typealias UpdateReCommentsLike = Resource<Boolean, String>
typealias UpdateCommentsCount = Resource<Boolean, String>

interface CommentRepository {
    suspend fun getComments(postKey: String): Flow<CommentsResource>
    suspend fun setComments(comments: Comments): AddComments
    suspend fun getReComments(recomments: Comments): Flow<ReCommentsResource>
    suspend fun setReComments(recomments: Comments): AddReComments
    suspend fun checkComment(commentKey: String): Flow<CheckComments>
    suspend fun checkReComment(recomments: Comments): Flow<CheckReComments>
    suspend fun updateCommentLike(
        commentKey: String,
        commentLikeList: ArrayList<String>
    ): UpdateCommentsLike

    suspend fun updateRecommentLike(
        comments: Comments,
        count: ArrayList<String>
    ): UpdateReCommentsLike

    suspend fun updateCommentsCount(postKey: String, count: String): UpdateCommentsCount
}