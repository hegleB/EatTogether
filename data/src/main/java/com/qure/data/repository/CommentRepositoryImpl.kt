package com.qure.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.qure.domain.model.Comments
import com.qure.domain.repository.CommentRepository
import com.qure.domain.utils.Constants
import com.qure.domain.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CommentRepository {

    override suspend fun getComments(postKey: String): Flow<Resource<List<Comments>, String>> {
        return callbackFlow {
            val callback = firestore.collection(Constants.COMMENTS_COLLECTION_PATH)
                .whereEqualTo("comments_postkey", postKey)
                .whereEqualTo("comments_depth", 0)
                .orderBy("comments_timestamp", Query.Direction.ASCENDING)
                .orderBy("comments_replyTimeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e == null) {
                        val isEmpty = snapshot?.isEmpty ?: false
                        val commets = snapshot?.toObjects(Comments::class.java) ?: listOf()
                        if (!isEmpty) {
                            this.trySendBlocking(Resource.Success(commets.reversed()))
                        } else {
                            this.trySendBlocking(Resource.Empty("데이터가 없습니다."))
                            this.trySendBlocking(Resource.Success(commets))
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

    override suspend fun setComments(comments: Comments): Flow<Resource<String, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback = firestore.collection(Constants.COMMENTS_COLLECTION_PATH).document(comments.comments_commentskey)
                .set(comments).addOnSuccessListener {
                    this.trySendBlocking(Resource.Success("댓글 추가 성공"))
                }.addOnFailureListener {
                    this.trySendBlocking(Resource.Error(it.message))
                }
            awaitClose {
                callback.isCanceled
            }
        }
    }

    override suspend fun getReComments(recomments: Comments): Flow<Resource<List<Comments>, String>> {
        return callbackFlow {
            val callback = firestore.collectionGroup(Constants.REPLY_COLLECTION_PATH)
                .whereEqualTo("comments_postkey", recomments.comments_postkey)
                .whereEqualTo("comments_depth", 1)
                .whereEqualTo("comments_commentskey", recomments.comments_commentskey)
                .orderBy("comments_timestamp", Query.Direction.ASCENDING)
                .orderBy("comments_replyTimeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e == null) {
                        val isEmpty = snapshot?.isEmpty ?: false
                        val commets = snapshot?.toObjects(Comments::class.java) ?: listOf()
                        if (!isEmpty) {
                            this.trySendBlocking(Resource.Success(commets))
                        } else {
                            this.trySendBlocking(Resource.Empty("데이터가 없습니다."))
                            this.trySendBlocking(Resource.Success(commets))
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

    override suspend fun setReComments(recomments: Comments): Flow<Resource<String, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback =
                firestore.collection(Constants.COMMENTS_COLLECTION_PATH).document(recomments.comments_commentskey)
                    .collection("reply")
                    .document()
                    .set(recomments).addOnSuccessListener {
                        this.trySendBlocking(Resource.Success("댓글 추가 성공"))
                    }.addOnFailureListener {
                        this.trySendBlocking(Resource.Error(it.message))
                    }
            awaitClose {
                callback.isCanceled
            }
        }
    }

    override suspend fun checkComment(commentKey: String): Flow<Resource<Comments, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback = firestore.collection(Constants.COMMENTS_COLLECTION_PATH).document(commentKey)
                .addSnapshotListener { snapshot, e ->
                    if (e == null) {
                        val isExists = snapshot?.exists() ?: false

                        if (isExists) {
                            val comment = snapshot?.toObject(Comments::class.java)!!
                            this.trySendBlocking(Resource.Success(comment))
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

    override suspend fun checkReComment(recomments: Comments): Flow<Resource<Comments, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback = firestore.collectionGroup(Constants.REPLY_COLLECTION_PATH)
                .whereEqualTo("comments_postkey", recomments.comments_postkey)
                .whereEqualTo("comments_depth", 1)
                .whereEqualTo("comments_commentskey", recomments.comments_commentskey)
                .whereEqualTo("comments_timestamp", recomments.comments_timestamp)
                .whereEqualTo("comments_replyTimeStamp", recomments.comments_replyTimeStamp)
                .addSnapshotListener { snapshot, e ->
                    if (e == null) {
                        val isEmpty = snapshot?.isEmpty ?: false

                        if (!isEmpty) {
                            val comment = snapshot?.toObjects(Comments::class.java)!!.get(0)
                            this.trySendBlocking(Resource.Success(comment))
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

    override suspend fun updateCommentLike(
        commentKey: String,
        commentLikeList: ArrayList<String>
    ): Flow<Resource<String, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback = firestore.collection(Constants.COMMENTS_COLLECTION_PATH).document(commentKey)
                .update("comments_likeCount", commentLikeList).addOnSuccessListener {
                    this.trySendBlocking(Resource.Success("성공"))
                }.addOnFailureListener {
                    this.trySendBlocking(Resource.Error(it.message))
                }
            awaitClose {
                callback.isCanceled
            }
        }
    }

    override suspend fun updateRecommentLike(
        recomments: Comments,
        count: ArrayList<String>
    ): Flow<Resource<String, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())

            val callback = firestore.collectionGroup(Constants.REPLY_COLLECTION_PATH)
                .whereEqualTo("comments_postkey", recomments.comments_postkey)
                .whereEqualTo("comments_depth", 1)
                .whereEqualTo("comments_commentskey", recomments.comments_commentskey)
                .whereEqualTo("comments_timestamp", recomments.comments_timestamp)
                .whereEqualTo("comments_replyTimeStamp", recomments.comments_replyTimeStamp)
                .get()
                .addOnSuccessListener { snapshot ->

                    val isEmpty = snapshot?.isEmpty ?: false
                    val commets = snapshot?.documents?.get(0)
                    if (!isEmpty) {
                        commets?.reference?.update("comments_likeCount", count)
                        this.trySendBlocking(Resource.Success("업데이트 성공"))
                    } else {
                        this.trySendBlocking(Resource.Empty("데이터가 없습니다."))
                    }
                }
            awaitClose {
                callback.isCanceled
            }
        }
    }

    override suspend fun updateCommentsCount(
        postKey: String,
        count: String
    ): Flow<Resource<String, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())

            val callback =
                firestore.collection(Constants.POSTS_COLLECTION_PATH)
                    .document(postKey)
                    .update("commentsCount", count)
                    .addOnSuccessListener { snapshot ->
                        this.trySendBlocking(Resource.Success("업데이트 성공"))
                    }
            awaitClose {
                callback.isCanceled
            }
        }

    }
}



