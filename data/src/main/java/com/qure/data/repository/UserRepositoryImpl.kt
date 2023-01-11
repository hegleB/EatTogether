package com.qure.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.qure.domain.model.User
import com.qure.domain.repository.UserRepository
import com.qure.domain.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun setUser(uid: String, user: User): Flow<Resource<String, String>> {

        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback = firestore.collection("users").document(uid ?: "").set(user).addOnSuccessListener {
                this.trySendBlocking(Resource.Success("유저 추가 성공"))
            }.addOnFailureListener {
                this.trySendBlocking(Resource.Error(it.message))
            }
            awaitClose {
                callback.isCanceled
            }
        }
    }

    override suspend fun getUser(uid: String): Flow<Resource<User, String>> {
        return callbackFlow {
            val callback = firestore.collection("users").document(uid ?: "").addSnapshotListener { snapshot, e ->
                this.trySendBlocking(Resource.Loading())
                val isExists = snapshot?.exists() ?: false
                if (e==null) {
                    if (isExists) {
                        val user = snapshot?.toObject(User::class.java)!!
                        this.trySendBlocking(Resource.Success(user))
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

    override suspend fun getAllUser(): Flow<Resource<List<User>, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback = firestore.collection("users").addSnapshotListener { snapshot, e ->

                val isEmpty = snapshot?.isEmpty ?: false
                try {
                    if (!isEmpty) {
                        val users = snapshot?.toObjects(User::class.java)!!.toMutableList()
                        this.trySendBlocking(Resource.Success(users))
                    } else {
                        this.trySendBlocking(Resource.Empty("데이터가 없습니다."))
                    }
                } catch (e: FirebaseFirestoreException) {
                    this.trySendBlocking(Resource.Error("FIrebaseFiStore Error"))
                }
            }
            awaitClose {
                callback.remove()
            }
        }
    }

    override suspend fun updateUser(
        uid: String,
        name: String,
        msg: String,
        image: String
    ): Flow<Resource<String, String>> {
        return callbackFlow {
            val userRef = firestore.collection("users").document(uid)
            val callback = firestore.runBatch {
                it.update(userRef, "usernm", name)
                it.update(userRef, "usermsg", msg)
                it.update(userRef, "userphoto", image)
            }
            awaitClose {
                callback.isCanceled
            }
        }
    }
}