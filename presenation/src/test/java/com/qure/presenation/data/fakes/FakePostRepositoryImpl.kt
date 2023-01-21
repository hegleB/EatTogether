package com.qure.presenation.data.fakes

import com.qure.domain.model.PostModel
import com.qure.domain.repository.*
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FakePostRepositoryImpl : PostRepository {
    override suspend fun setPost(post: PostModel.Post): AddPost {
        return if (post == null) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }

    override suspend fun getLikeCount(uid: String): Flow<LikeCountResource> {
        return callbackFlow {
            if (uid.isNullOrBlank()) {
                Resource.Success(false)
            } else {
                Resource.Success(true)
            }
        }
    }

    override suspend fun getPostCount(uid: String): Flow<PostCountResource> {
        return callbackFlow {
            if (uid.isNullOrBlank()) {
                Resource.Success(false)
            } else {
                Resource.Success(true)
            }
        }
    }

    override suspend fun getAllPost(): Flow<PostsResource> {
        return callbackFlow {
            Resource.Success(true)
        }
    }


    override suspend fun updateLike(likeList: ArrayList<String>, postKey: String): UpdateLike {
        return if (postKey.isNullOrBlank()) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }

    override suspend fun checkPost(postKey: String): Flow<CheckPost> {
        return callbackFlow {
            if (postKey.isNullOrBlank()) {
                Resource.Success(false)
            } else {
                Resource.Success(true)
            }
        }
    }

    override suspend fun getCategoryPost(categoryName: String): Flow<CategoryPostResource> {
        return callbackFlow {
            if (categoryName.isNullOrBlank()) {
                Resource.Success(false)
            } else {
                Resource.Success(true)
            }
        }
    }

    override suspend fun getProfileCreatedPosts(uid: String): Flow<CreatedPostsResource> {
        return callbackFlow {
            if (uid.isNullOrBlank()) {
                Resource.Success(false)
            } else {
                Resource.Success(true)
            }
        }
    }

    override suspend fun getProfileLikedPosts(uid: String): Flow<LikedPostsResource> {
        return callbackFlow {
            if (uid.isNullOrBlank()) {
                Resource.Success(false)
            } else {
                Resource.Success(true)
            }
        }
    }

    override suspend fun getProfileCommentsCreatedPosts(uid: String): Flow<CommentsCreatedPostsResource> {
        return callbackFlow {
            if (uid.isNullOrBlank()) {
                Resource.Success(false)
            } else {
                Resource.Success(true)
            }
        }
    }

    override suspend fun getPostImage(postKey: String): Flow<PostImageResource> {
        return callbackFlow {
            if (postKey.isNullOrBlank()) {
                Resource.Success(false)
            } else {
                Resource.Success(true)
            }
        }
    }
}