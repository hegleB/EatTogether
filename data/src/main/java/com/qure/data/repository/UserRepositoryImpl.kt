package com.qure.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.qure.domain.model.User
import com.qure.domain.repository.AddUser
import com.qure.domain.repository.UpdateUser
import com.qure.domain.repository.UserRepository
import com.qure.domain.utils.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun setUser(uid: String, user: User): AddUser {
        return try {
            firestore.collection(USERS_COLLECTION_PATH)
                .document(uid ?: "")
                .set(user)
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun getUser(uid: String) = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collection(USERS_COLLECTION_PATH)
            .document(uid ?: "")
            .addSnapshotListener { snapshot, e ->
                val userResource = if (snapshot != null) {
                    val user = snapshot.toObject(User::class.java) ?: User()
                    Resource.Success(user)
                } else {
                    Resource.Error(e?.message)
                }
                trySend(userResource)
            }
        awaitClose {
            callback.remove()
        }
    }

    override suspend fun getAllUser() = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collection(USERS_COLLECTION_PATH)
            .addSnapshotListener { snapshot, e ->
                val usersResource = if (snapshot != null) {
                    val users = snapshot.toObjects(User::class.java)
                    Resource.Success(users)
                } else {
                    Resource.Error(e?.message)
                }
                trySend(usersResource)
            }
        awaitClose {
            callback.remove()
        }

    }

    override suspend fun updateUser(
        uid: String,
        name: String,
        msg: String,
        image: String
    ): UpdateUser {
        return try {
            val userRef = firestore.collection(USERS_COLLECTION_PATH).document(uid)
            firestore.runBatch {
                it.update(userRef, USER_NAME_FIELD, name)
                it.update(userRef, USER_MESSAGE_FIELD, msg)
                it.update(userRef, USER_PHOTO_FIELD, image)
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}