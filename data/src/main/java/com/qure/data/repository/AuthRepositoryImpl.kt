package com.qure.data.repository

import android.util.Log
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.qure.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override fun signInWithGoogle(credential: AuthCredential) : Task<AuthResult> {
        return firebaseAuth.signInWithCredential(credential)
    }

    override fun signInWithFacebook(token: AccessToken) : Task<AuthResult> {
        return firebaseAuth.signInWithCredential(FacebookAuthProvider.getCredential(token.token))
    }


    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun isJoin(user: FirebaseUser): Boolean? {
        return firestore.collection("users").document(user.uid).id == null
    }

    override fun signOutUser() {
        firebaseAuth.signOut()
    }

    override suspend fun geMessageToken(): Task<String> {
        return FirebaseMessaging.getInstance().token
    }

}