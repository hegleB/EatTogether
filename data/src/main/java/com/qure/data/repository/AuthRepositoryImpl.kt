package com.qure.data.repository

import com.facebook.AccessToken
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.qure.domain.repository.AuthRepository
import com.qure.domain.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override fun signInWithGoogle(credential: AuthCredential): Task<AuthResult> {
        return firebaseAuth.signInWithCredential(credential)
    }

    override fun signInWithFacebook(token: AccessToken): Task<AuthResult> {
        return firebaseAuth.signInWithCredential(FacebookAuthProvider.getCredential(token.token))
    }


    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override suspend fun isJoin(user: FirebaseUser): Flow<Resource<Boolean, String>> {
        return callbackFlow {
            val callback = firestore.collection("users")
                .document(user.uid)
                .addSnapshotListener { snapshot, e ->
                    if (e == null) {
                        val isExists = snapshot?.exists() ?: false
                        if (isExists) {
                            this.trySendBlocking(Resource.Success(true))
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

    override fun signOutUser() {
        firebaseAuth.signOut()
    }

    override suspend fun geMessageToken(): Task<String> {
        return FirebaseMessaging.getInstance().token
    }

}