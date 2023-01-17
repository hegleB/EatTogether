package com.qure.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.qure.domain.model.Comments
import com.qure.domain.repository.*
import com.qure.domain.utils.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CommentRepository {

    override suspend fun getComments(postKey: String) = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collection(COMMENTS_COLLECTION_PATH)
            .whereEqualTo(COMMENTS_POST_KEY_FIELD, postKey)
            .whereEqualTo(COMMENTS_DEPTH_FIELD, 0)
            .orderBy(COMMENTS_TIMESTAMP_FIELD, Query.Direction.ASCENDING)
            .orderBy(COMMENTS_REPLY_TIMESTAMP_FIELD, Query.Direction.ASCENDING)
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
            firestore.collection(COMMENTS_COLLECTION_PATH)
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
        val callback = firestore.collectionGroup(REPLY_COLLECTION_PATH)
            .whereEqualTo(COMMENTS_POST_KEY_FIELD, recomments.comments_postkey)
            .whereEqualTo(COMMENTS_DEPTH_FIELD, 1)
            .whereEqualTo(COMMENTS_COMMENT_KEY_FIELD, recomments.comments_commentskey)
            .orderBy(COMMENTS_TIMESTAMP_FIELD, Query.Direction.ASCENDING)
            .orderBy(COMMENTS_REPLY_TIMESTAMP_FIELD, Query.Direction.ASCENDING)
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
            firestore.collection(COMMENTS_COLLECTION_PATH)
                .document(recomments.comments_commentskey)
                .collection(REPLY_COLLECTION_PATH)
                .document(recomments.comments_replyKey)
                .set(recomments)
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun checkComment(commentKey: String) = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collection(COMMENTS_COLLECTION_PATH)
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
        val callback = firestore.collectionGroup(REPLY_COLLECTION_PATH)
            .whereEqualTo(COMMENTS_POST_KEY_FIELD, recomments.comments_postkey)
            .whereEqualTo(COMMENTS_DEPTH_FIELD, 1)
            .whereEqualTo(COMMENTS_COMMENT_KEY_FIELD, recomments.comments_commentskey)
            .whereEqualTo(COMMENTS_TIMESTAMP_FIELD, recomments.comments_timestamp)
            .whereEqualTo(COMMENTS_REPLY_TIMESTAMP_FIELD, recomments.comments_replyTimeStamp)
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
            firestore.collection(COMMENTS_COLLECTION_PATH)
                .document(commentKey)
                .update(COMMENTS_LIKE_COUNT_FIELD, commentLikeList)
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
            firestore.collectionGroup(REPLY_COLLECTION_PATH)
                .whereEqualTo(COMMENTS_POST_KEY_FIELD, recomments.comments_postkey)
                .whereEqualTo(COMMENTS_DEPTH_FIELD, 1)
                .whereEqualTo(COMMENTS_COMMENT_KEY_FIELD, recomments.comments_commentskey)
                .whereEqualTo(COMMENTS_TIMESTAMP_FIELD, recomments.comments_timestamp)
                .whereEqualTo(COMMENTS_REPLY_TIMESTAMP_FIELD, recomments.comments_replyTimeStamp)
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
            firestore.collection(POSTS_COLLECTION_PATH)
                .document(postKey)
                .update(COMMENTS_COUNT_FIELD, count)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}



