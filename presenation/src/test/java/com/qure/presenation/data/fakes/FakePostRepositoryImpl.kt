package com.qure.presenation.data.fakes

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.qure.domain.model.Comments
import com.qure.domain.model.PostModel
import com.qure.domain.repository.*
import com.qure.domain.utils.Resource
import com.qure.presenation.data.utils.TestDataUtils
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

class FakePostRepositoryImpl : PostRepository {

    private val posts = TestDataUtils.posts
    private val comments = TestDataUtils.comments

    override suspend fun setPost(post: PostModel.Post): AddPost {
        val addedPost = posts.toMutableList()
        addedPost.add(PostModel.Post(key = "c", title = "c", content = "c", category = "일식"))
        return if (posts.size + 1 == addedPost.size) {
            Resource.Success(true)
        } else {
            Resource.Success(false)
        }
    }

    override suspend fun getLikeCount(uid: String): Flow<LikeCountResource> {
        val likeCount = posts.find { it.isSameUid(uid) }?.likecount?.size ?: 0
        return flowOf(Resource.Success(likeCount))
    }

    override suspend fun getPostCount(uid: String): Flow<PostCountResource> {
        val postCount = posts.filter { it.isSameUid(uid) }.size
        return flowOf(Resource.Success(postCount))
    }

    override suspend fun getAllPost(): Flow<PostsResource> {
        return flowOf(Resource.Success(posts))
    }


    override suspend fun updateLike(likeList: ArrayList<String>, postKey: String): UpdateLike {
        val post = posts.find { it.isSameKey(postKey) } ?: PostModel.Post()
        val updatedPost = post.copy().updateLike(likeList)
        return if (updatedPost.isSameLikeCount(likeList.size)) {
            Resource.Success(true)
        } else {
            Resource.Success(false)
        }
    }

    override suspend fun checkPost(postKey: String): Flow<CheckPost> {
        val post = posts.find { it.isSameKey(postKey) } ?: PostModel.Post()
        return flowOf(Resource.Success(post))

    }

    override suspend fun getCategoryPost(categoryName: String): Flow<CategoryPostResource> {
        val categoryPost = posts.filter { it.isSameCategory(categoryName) }
        return flowOf(Resource.Success(categoryPost))
    }

    override suspend fun getProfileCreatedPosts(uid: String): Flow<CreatedPostsResource> {
        var posts = posts.filter { it.isSameUid(uid) }
        posts = posts.sortedByDescending { it.timestamp }
        return flowOf(Resource.Success(posts))
    }

    override suspend fun getProfileLikedPosts(uid: String): Flow<LikedPostsResource> {
        var posts = posts.filter { it.isClickedPostLike(uid) }
        posts = posts.sortedByDescending { it.timestamp }
        return flowOf(Resource.Success(posts))
    }

    override suspend fun getProfileCommentsCreatedPosts(uid: String): Flow<CommentsCreatedPostsResource> {
        val filterdSameUidComments = comments.filter { it.isSameCommentUid(uid) }
        val filteredPosts = mutableSetOf<PostModel.Post>()
        for (post in posts) {
            for (comment in filterdSameUidComments) {
                if (post.isSameKey(comment.comments_postkey)) {
                    filteredPosts.add(post)
                }
            }
        }
        val result = filteredPosts.toMutableSet().sortedByDescending { it.timestamp }
        return flowOf(Resource.Success(result.toList()))
    }

    override suspend fun getPostImage(postKey: String): Flow<PostImageResource> {
        val postImages = posts.find { it.isSameKey(postKey) }?.postImages ?: arrayListOf()
        val images = mutableListOf<PostModel.PostImage>()
        for (image in postImages) {
            images.add(PostModel.PostImage(postKey, image))
        }
        return flowOf(Resource.Success(images))
    }
}