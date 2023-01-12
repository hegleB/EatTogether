package com.qure.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.qure.domain.model.User
import com.qure.domain.repository.AddUser
import com.qure.domain.repository.UpdateUser
import com.qure.domain.repository.UserRepository
import com.qure.domain.utils.Constants
import com.qure.domain.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun setUser(uid: String, user: User): AddUser {
        return try {
            firestore.collection(Constants.USERS_COLLECTION_PATH)
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
        val callback = firestore.collection(Constants.USERS_COLLECTION_PATH)
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
        val callback = firestore.collection(Constants.USERS_COLLECTION_PATH)
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
            val userRef = firestore.collection(Constants.USERS_COLLECTION_PATH).document(uid)
            firestore.runBatch {
                it.update(userRef, "usernm", name)
                it.update(userRef, "usermsg", msg)
                it.update(userRef, "userphoto", image)
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}