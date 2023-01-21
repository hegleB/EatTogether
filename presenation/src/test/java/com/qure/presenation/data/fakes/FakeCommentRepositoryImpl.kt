package com.qure.presenation.data.fakes

import com.qure.domain.model.Comments
import com.qure.domain.repository.*
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FakeCommentRepositoryImpl: CommentRepository {
    override suspend fun getComments(postKey: String): Flow<CommentsResource> {
        return callbackFlow {
            if (postKey.isNullOrBlank()) {
                Resource.Success(false)
            } else {
                Resource.Success(true)
            }
        }
    }

    override suspend fun setComments(comments: Comments): AddComments {
        return if (comments == null) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }

    override suspend fun getReComments(recomments: Comments): Flow<ReCommentsResource> {
        return callbackFlow {
            if (recomments == null) {
                Resource.Success(false)
            } else {
                Resource.Success(true)
            }
        }
    }

    override suspend fun setReComments(recomments: Comments): AddReComments {
        return if (recomments == null) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }

    override suspend fun checkComment(commentKey: String): Flow<CheckComments> {
        return callbackFlow {
            if (commentKey.isNullOrBlank()) {
                Resource.Success(false)
            } else {
                Resource.Success(true)
            }
        }
    }

    override suspend fun checkReComment(recomments: Comments): Flow<CheckReComments> {
        return callbackFlow {
            if (recomments == null) {
                Resource.Success(false)
            } else {
                Resource.Success(true)
            }
        }
    }

    override suspend fun updateCommentLike(
        commentKey: String,
        commentLikeList: ArrayList<String>
    ): UpdateCommentsLike {
        return if (commentKey.isNullOrBlank()) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }

    override suspend fun updateRecommentLike(
        comments: Comments,
        count: ArrayList<String>
    ): UpdateReCommentsLike {
        return if (comments == null) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }

    override suspend fun updateCommentsCount(postKey: String, count: String): UpdateCommentsCount {
        return if (postKey.isNullOrBlank()) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }
}