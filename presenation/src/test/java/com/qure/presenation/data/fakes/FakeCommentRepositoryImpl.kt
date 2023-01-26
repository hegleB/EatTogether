package com.qure.presenation.data.fakes

import com.qure.domain.model.Comments
import com.qure.domain.model.PostModel
import com.qure.domain.repository.*
import com.qure.domain.utils.Resource
import com.qure.presenation.data.utils.TestDataUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf

class FakeCommentRepositoryImpl : CommentRepository {

    private val posts = TestDataUtils.posts
    private val comments = TestDataUtils.comments
    private val recomments = TestDataUtils.recomments
    override suspend fun getComments(postKey: String): Flow<CommentsResource> {
        val comments = TestDataUtils.comments.filter { it.isSamePostKey(postKey) }
        return flowOf(Resource.Success(comments))
    }

    override suspend fun setComments(comment: Comments): AddComments {
        val addedComments = comments.toMutableList()
        addedComments.add(comment)
        return if (comments.size + 1 == addedComments.size) {
            Resource.Success(true)
        } else {
            Resource.Success(false)
        }
    }

    override suspend fun getReComments(recomment: Comments): Flow<ReCommentsResource> {
        val recomments =
            TestDataUtils.recomments.filter {
                it.isSamePostKey(recomment.comments_postkey)
                        && it.comments_depth == 1
                        && it.isSameCommentKey(recomment.comments_commentskey)
            }.sortedByDescending { it.comments_replyTimeStamp }
        return flowOf(Resource.Success(recomments))
    }

    override suspend fun setReComments(recomment: Comments): AddReComments {
        val addedReComments = recomments.toMutableList()
        addedReComments.add(recomment)
        return if (recomments.size + 1 == addedReComments.size) {
            Resource.Success(true)
        } else {
            Resource.Success(false)
        }
    }

    override suspend fun checkComment(commentKey: String): Flow<CheckComments> {
        return callbackFlow {
            if (comments.filter { it.isSameCommentKey(commentKey) }.isEmpty()) {
                Resource.Success(false)
            } else {
                Resource.Success(true)
            }
        }
    }

    override suspend fun checkReComment(recomment: Comments): Flow<CheckReComments> {
        return callbackFlow {
            if (recomments.contains(recomment)) {
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
        val foundComment = comments.find { it.isSameCommentKey(commentKey) } ?: Comments()
        val updatedComment = foundComment.copy(comments_likeCount = commentLikeList)
        return if (updatedComment.comments_likeCount == commentLikeList) {
            Resource.Success(true)
        } else {
            Resource.Success(false)
        }
    }

    override suspend fun updateRecommentLike(
        recomment: Comments,
        count: ArrayList<String>
    ): UpdateReCommentsLike {
        val foundRecomment = recomments.find { it.isSameRecommentsKey(recomment.comments_replyKey) } ?: Comments()
        val updatedRecomment = foundRecomment.copy(comments_likeCount = recomment.comments_likeCount)
        return if (updatedRecomment.comments_likeCount == recomment.comments_likeCount) {
            Resource.Success(true)
        } else {
            Resource.Success(false)
        }
    }

    override suspend fun updateCommentsCount(postKey: String, count: String): UpdateCommentsCount {
        val foundPost = posts.find { it.isSameKey(postKey) } ?: PostModel.Post()
        val updatedRecomment = foundPost.copy(commentsCount = count)
        return if (updatedRecomment.commentsCount == count) {
            Resource.Success(true)
        } else {
            Resource.Success(false)
        }
    }
}