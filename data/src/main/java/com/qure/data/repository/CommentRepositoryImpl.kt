package com.qure.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.qure.domain.model.Comments
import com.qure.domain.repository.*
import com.qure.domain.utils.Constants
import com.qure.domain.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CommentRepository {

    override suspend fun getComments(postKey: String) = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collection(Constants.COMMENTS_COLLECTION_PATH)
            .whereEqualTo("comments_postkey", postKey)
            .whereEqualTo("comments_depth", 0)
            .orderBy("comments_timestamp", Query.Direction.ASCENDING)
            .orderBy("comments_replyTimeStamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                val commentsResource = if (snapshot != null) {
                    val commets = snapshot.toObjects(Comments::class.java)
                    Resource.Success(commets)
                } else {
                    Resource.Error(e?.message)
                }
                trySend(commentsResource)
            }
        awaitClose {
            callback.remove()
        }
    }

    override suspend fun setComments(comments: Comments): AddComments {
        return try {
            firestore.collection(Constants.COMMENTS_COLLECTION_PATH)
                .document(comments.comments_commentskey)
                .set(comments)
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun getReComments(recomments: Comments) = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collectionGroup(Constants.REPLY_COLLECTION_PATH)
            .whereEqualTo("comments_postkey", recomments.comments_postkey)
            .whereEqualTo("comments_depth", 1)
            .whereEqualTo("comments_commentskey", recomments.comments_commentskey)
            .orderBy("comments_timestamp", Query.Direction.ASCENDING)
            .orderBy("comments_replyTimeStamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                val reCommentsResource = if (snapshot != null) {
                    val commets = snapshot.toObjects(Comments::class.java)
                    Resource.Success(commets)
                } else {
                    Resource.Error(e?.message)
                }
                trySend(reCommentsResource)
            }
        awaitClose {
            callback.remove()
        }
    }

    override suspend fun setReComments(recomments: Comments): AddReComments {
        return try {
            firestore.collection(Constants.COMMENTS_COLLECTION_PATH)
                .document(recomments.comments_commentskey)
                .collection(Constants.REPLY_COLLECTION_PATH)
                .document()
                .set(recomments)
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun checkComment(commentKey: String) = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collection(Constants.COMMENTS_COLLECTION_PATH)
            .document(commentKey)
            .addSnapshotListener { snapshot, e ->
                val commentResource = if (snapshot != null) {
                    val comment = snapshot.toObject(Comments::class.java)
                    Resource.Success(comment ?: Comments())
                } else {
                    Resource.Error(e?.message)
                }
                trySend(commentResource)
            }
        awaitClose {
            callback.remove()
        }
    }

    override suspend fun checkReComment(recomments: Comments) = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collectionGroup(Constants.REPLY_COLLECTION_PATH)
            .whereEqualTo("comments_postkey", recomments.comments_postkey)
            .whereEqualTo("comments_depth", 1)
            .whereEqualTo("comments_commentskey", recomments.comments_commentskey)
            .whereEqualTo("comments_timestamp", recomments.comments_timestamp)
            .whereEqualTo("comments_replyTimeStamp", recomments.comments_replyTimeStamp)
            .addSnapshotListener { snapshot, e ->
                val recommentResource = if (snapshot != null && !snapshot.isEmpty) {
                    val recomment = snapshot.toObjects(Comments::class.java).get(0)
                    Resource.Success(recomment)
                } else {
                    Resource.Error(e?.message)
                }
                trySend(recommentResource)
            }
        awaitClose {
            callback.remove()
        }
    }

    override suspend fun updateCommentLike(
        commentKey: String,
        commentLikeList: ArrayList<String>
    ): UpdateCommentsLike {
        return try {
            firestore.collection(Constants.COMMENTS_COLLECTION_PATH)
                .document(commentKey)
                .update("comments_likeCount", commentLikeList)
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun updateRecommentLike(
        recomments: Comments,
        count: ArrayList<String>
    ): UpdateReCommentsLike {
        return try {
            firestore.collectionGroup(Constants.REPLY_COLLECTION_PATH)
                .whereEqualTo("comments_postkey", recomments.comments_postkey)
                .whereEqualTo("comments_depth", 1)
                .whereEqualTo("comments_commentskey", recomments.comments_commentskey)
                .whereEqualTo("comments_timestamp", recomments.comments_timestamp)
                .whereEqualTo("comments_replyTimeStamp", recomments.comments_replyTimeStamp)
                .get()
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun updateCommentsCount(
        postKey: String,
        count: String
    ): UpdateCommentsCount {
        return try {
            firestore.collection(Constants.POSTS_COLLECTION_PATH)
                .document(postKey)
                .update("commentsCount", count)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}



