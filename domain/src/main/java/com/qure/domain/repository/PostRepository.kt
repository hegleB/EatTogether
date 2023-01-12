package com.qure.domain.repository

import com.qure.domain.model.PostModel
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

typealias AddPost = Resource<Boolean, String>
typealias LikeCountResource = Resource<Int, String>
typealias PostCountResource = Resource<Int, String>
typealias PostsResource = Resource<List<PostModel.Post>, String>
typealias UpdateLike = Resource<Boolean, String>
typealias CheckPost = Resource<PostModel.Post, String>
typealias CategoryPostResource = Resource<List<PostModel.Post>, String>
typealias CreatedPostsResource = Resource<List<PostModel.Post>, String>
typealias LikedPostsResource = Resource<List<PostModel.Post>, String>
typealias CommentsCreatedPostsResource = Resource<List<PostModel.Post>, String>

interface PostRepository {
    suspend fun setPost(post: PostModel.Post): AddPost
    suspend fun getLikeCount(uid: String): Flow<LikeCountResource>
    suspend fun getPostCount(uid: String): Flow<PostCountResource>
    suspend fun getAllPost(): Flow<PostsResource>
    suspend fun updateLike(
        likeList: ArrayList<String>,
        postKey: String
    ): UpdateLike

    suspend fun checkPost(postKey: String): Flow<CheckPost>
    suspend fun getCategoryPost(categoryName: String): Flow<CategoryPostResource>
    suspend fun getProfileCreatedPosts(uid: String): Flow<CreatedPostsResource>
    suspend fun getProfileLikedPosts(uid: String): Flow<LikedPostsResource>
    suspend fun getProfileCommentsCreatedPosts(uid: String): Flow<CommentsCreatedPostsResource>
}