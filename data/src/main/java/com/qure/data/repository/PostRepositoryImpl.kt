package com.qure.data.repository


import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.qure.domain.model.Comments
import com.qure.domain.model.PostModel
import com.qure.domain.model.PostModel.Post
import com.qure.domain.repository.AddPost
import com.qure.domain.repository.PostRepository
import com.qure.domain.repository.UpdateLike
import com.qure.domain.utils.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PostRepository {

    override suspend fun setPost(post: Post): AddPost {
        return try {
            firestore.collection(POSTS_COLLECTION_PATH)
                .document(post.key)
                .set(post)
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun getLikeCount(uid: String) = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collection(POSTS_COLLECTION_PATH)
            .whereArrayContains(LIKE_COUNT_FIELD, uid)
            .addSnapshotListener { snapshot, e ->
                val likeCountResource = if (snapshot != null) {
                    val like = snapshot.toObjects(Post::class.java).size
                    Resource.Success(like)
                } else {
                    Resource.Error(e?.message)
                }
                trySend(likeCountResource)
            }
        awaitClose {
            callback.remove()
        }
    }

    override suspend fun getPostCount(uid: String) = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collection(POSTS_COLLECTION_PATH)
            .whereEqualTo(UID_FIELD, uid)
            .addSnapshotListener { snapshot, e ->
                val postCountResource = if (snapshot != null) {
                    val post = snapshot.toObjects(Post::class.java).size
                    Resource.Success(post)
                } else {
                    Resource.Error(e?.message)
                }
                trySend(postCountResource)
            }
        awaitClose {
            callback.remove()
        }
    }


    override suspend fun getAllPost() = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collection(POSTS_COLLECTION_PATH)
            .orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                val postsResource = if (snapshot != null) {
                    val post = snapshot.toObjects(Post::class.java)
                    Resource.Success(post)
                } else {
                    Resource.Error(e?.message)
                }
                trySend(postsResource)
            }
        awaitClose {
            callback.remove()
        }
    }

    override suspend fun updateLike(likeList: ArrayList<String>, postKey: String): UpdateLike {
        return try {
            firestore.collection(POSTS_COLLECTION_PATH)
                .document(postKey)
                .update(LIKE_COUNT_FIELD, likeList)
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun checkPost(postKey: String) = callbackFlow {
        trySend(Resource.Loading())
        val callback =
            firestore.collection(POSTS_COLLECTION_PATH).document(postKey)
                .addSnapshotListener { snapshot, e ->
                    val postResouce = if (snapshot != null) {
                        val post = snapshot?.toObject(Post::class.java)!!
                        Resource.Success(post)
                    } else {
                        Resource.Error(e?.message)
                    }
                    trySend(postResouce)
                }
        awaitClose {
            callback.remove()
        }
    }


    override suspend fun getCategoryPost(categoryName: String) = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collection(POSTS_COLLECTION_PATH)
            .whereEqualTo(CATEGORY_FIELD, categoryName)
            .addSnapshotListener { snapshot, e ->
                val categoryPostResource = if (snapshot != null) {
                    val post = snapshot?.toObjects(Post::class.java)!!
                    Resource.Success(post)
                } else {
                    Resource.Error(e?.message)
                }
                trySend(categoryPostResource)
            }
        awaitClose {
            callback.remove()
        }
    }


    override suspend fun getProfileCreatedPosts(uid: String) = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collection(POSTS_COLLECTION_PATH)
            .whereEqualTo(UID_FIELD, uid)
            .orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                val createdPostsResource = if (snapshot != null) {
                    val post = snapshot?.toObjects(Post::class.java)!!
                    Resource.Success(post)
                } else {
                    Resource.Error(e?.message)
                }
                trySend(createdPostsResource)
            }
        awaitClose {
            callback.remove()
        }
    }


    override suspend fun getProfileLikedPosts(uid: String) = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collection(POSTS_COLLECTION_PATH)
            .whereArrayContains(LIKE_COUNT_FIELD, uid)
            .orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                val likePostsResource = if (snapshot != null) {
                    val post = snapshot?.toObjects(Post::class.java)!!
                    Resource.Success(post)
                } else {
                    Resource.Error(e?.message)
                }
                trySend(likePostsResource)
            }
        awaitClose {
            callback.remove()
        }
    }


    override suspend fun getProfileCommentsCreatedPosts(uid: String) = callbackFlow {
        trySend(Resource.Loading())
        val commentCreatedsPost = mutableListOf<Post>()
        val callback =
            firestore.collection(POSTS_COLLECTION_PATH)
                .orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener { post ->
                    firestore.collection(COMMENTS_COLLECTION_PATH)
                        .whereEqualTo(COMMENTS_UID_FIELD, uid)
                        .get()
                        .addOnCompleteListener { snapshot ->
                            val posts = post.result.toObjects(Post::class.java)
                            val commentsPost = snapshot.result.toObjects(Comments::class.java)
                            for (post in posts) {
                                for (comment in commentsPost) {
                                    if (post.key.equals(comment.comments_postkey) && !commentCreatedsPost.contains(
                                            post
                                        )
                                    ) {
                                        commentCreatedsPost.add(post)
                                    }
                                }
                            }
                            trySend(Resource.Success(commentCreatedsPost))
                        }
                }
        awaitClose {
            callback.isCanceled
        }
    }

    override suspend fun getPostImage(postKey: String) = callbackFlow {
        trySend(Resource.Loading())
        val callback =
            firestore.collectionGroup(IMAGES_COLLECTION_GROUP)
                .whereEqualTo(POST_KEY_FIELD, postKey)
                .orderBy(POST_IMAGE_FIELD, Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, e ->
                    val postImageResource = if (snapshot != null) {
                        val postimages = snapshot.toObjects(PostModel.PostImage::class.java)
                        Resource.Success(postimages)
                    } else {
                        Resource.Error(e?.message)
                    }
                    trySend(postImageResource)
                }
        awaitClose {
            callback.remove()
        }
    }
}