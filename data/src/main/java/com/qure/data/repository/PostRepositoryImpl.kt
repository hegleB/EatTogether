package com.qure.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.qure.domain.model.PostModel.Post
import com.qure.domain.repository.PostRepository
import com.qure.domain.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PostRepository{

    override suspend fun getLikeCount(uid: String): Flow<Resource<Int, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback = firestore.collection("posts").whereArrayContains("likecount",uid).addSnapshotListener { snapshot, e ->
                if (e==null) {
                    val isEmpty = snapshot?.isEmpty ?: false
                    val like = snapshot?.toObjects(Post::class.java)?.size ?: 0
                    if (!isEmpty) {
                        this.trySendBlocking(Resource.Success(like))
                    } else {
                        this.trySendBlocking(Resource.Empty("데이터가 없습니다."))
                        this.trySendBlocking(Resource.Success(like))
                    }
                } else {
                    this.trySendBlocking(Resource.Error(e.message))
                }
            }
            awaitClose {
                callback.remove()
            }
        }
    }

    override suspend fun getPostCount(uid: String): Flow<Resource<Int, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback = firestore.collection("posts").whereEqualTo("uid",uid).addSnapshotListener { snapshot, e ->
                if (e==null) {
                    val isEmpty = snapshot?.isEmpty ?: false
                    val post = snapshot?.toObjects(Post::class.java)?.size!!
                    if (!isEmpty) {
                        this.trySendBlocking(Resource.Success(post))
                    } else {
                        this.trySendBlocking(Resource.Empty("데이터가 없습니다."))
                        this.trySendBlocking(Resource.Success(post))
                    }
                } else {
                    this.trySendBlocking(Resource.Error(e.message))
                }
            }
            awaitClose {
                callback.remove()
            }
        }
    }

    override suspend fun getAllPost(): Flow<Resource<List<Post>,String>> {
        return callbackFlow {
            val callback = firestore.collection("posts")
                .orderBy("timestamp",  Query.Direction.DESCENDING).addSnapshotListener { snapshot, e ->
                    if (e==null) {
                        val isEmpty = snapshot?.isEmpty ?: false

                        if (!isEmpty) {
                            val post = snapshot?.toObjects(Post::class.java) ?: listOf()
                            this.trySendBlocking(Resource.Success(post))
                        } else {
                            this.trySendBlocking(Resource.Empty("데이터가 없습니다."))
                        }
                    } else {
                        this.trySendBlocking(Resource.Error(e.message))
                    }
            }
            awaitClose {
                callback.remove()
            }
        }
    }

    override suspend fun updateLike(likeList: ArrayList<String>, postKey : String): Flow<Resource<String, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback = firestore.collection("posts").document(postKey).update("likecount", likeList).addOnSuccessListener {
                this.trySendBlocking(Resource.Success("성공"))
            }.addOnFailureListener {
                this.trySendBlocking(Resource.Error(it.message))
            }
            awaitClose {
                callback.isCanceled
            }
        }
    }

    override suspend fun checkPost(postKey: String): Flow<Resource<Post, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback = firestore.collection("posts").document(postKey).addSnapshotListener { snapshot, e ->
                if (e==null) {
                    val isExists = snapshot?.exists() ?: false

                    if (isExists) {
                        val post = snapshot?.toObject(Post::class.java)!!
                        this.trySendBlocking(Resource.Success(post))
                    } else {
                        this.trySendBlocking(Resource.Empty("데이터가 없습니다."))
                    }
                } else {
                    this.trySendBlocking(Resource.Error(e.message))
                }
            }
            awaitClose {
                callback.remove()
            }
        }
    }


}