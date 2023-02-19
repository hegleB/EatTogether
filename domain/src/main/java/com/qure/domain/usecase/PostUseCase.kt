package com.qure.domain.usecase

import android.net.Uri
import com.qure.domain.model.PostModel
import com.qure.domain.repository.PostRepository
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend fun checkPost(postKey: String) = postRepository.checkPost(postKey)
    suspend fun getAllPost() = postRepository.getAllPost()
    suspend fun getCategoryPost(categoryName: String) = postRepository.getCategoryPost(categoryName)
    suspend fun getLikeCount(uid: String) = postRepository.getLikeCount(uid)
    suspend fun getPostCount(uid: String) = postRepository.getPostCount(uid)
    suspend fun getPostImage(postKey: String) = postRepository.getPostImage(postKey)
    suspend fun getProfileCommentsCreatedPosts(uid: String) =
        postRepository.getProfileCommentsCreatedPosts(uid)

    suspend fun getProfileCreatedPosts(uid: String) = postRepository.getProfileCreatedPosts(uid)
    suspend fun getProfileLikedPosts(uid: String) = postRepository.getProfileLikedPosts(uid)
    suspend fun setPost(post: PostModel.Post) = postRepository.setPost(post)
    suspend fun updateLike(likeList: ArrayList<String>, postKey: String) =
        postRepository.updateLike(likeList, postKey)
}