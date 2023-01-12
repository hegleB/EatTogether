package com.qure.domain.repository

import com.qure.domain.model.Comments
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CommentRepository {

    suspend fun getComments(postKey: String): Flow<Resource<List<Comments>, String>>
    suspend fun setComments(comments: Comments): Flow<Resource<String, String>>
    suspend fun getReComments(recomments: Comments): Flow<Resource<List<Comments>, String>>
    suspend fun setReComments(recomments: Comments): Flow<Resource<String, String>>
    suspend fun checkComment(commentKey: String): Flow<Resource<Comments, String>>
    suspend fun checkReComment(recomments: Comments): Flow<Resource<Comments, String>>
    suspend fun updateCommentLike(
        commentKey: String,
        commentLikeList: ArrayList<String>
    ): Flow<Resource<String, String>>

    suspend fun updateRecommentLike(
        comments: Comments,
        count: ArrayList<String>
    ): Flow<Resource<String, String>>

    suspend fun updateCommentsCount(postKey: String, count: String): Flow<Resource<String, String>>
}