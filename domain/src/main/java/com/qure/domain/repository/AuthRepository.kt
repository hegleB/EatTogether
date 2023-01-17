package com.qure.domain.repository

import com.facebook.AccessToken
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser


interface AuthRepository {

    fun signInWithGoogle(credential: AuthCredential): Task<AuthResult>
    fun signInWithFacebook(token: AccessToken): Task<AuthResult>
    fun getCurrentUser(): FirebaseUser?
    fun signOutUser()
    suspend fun geMessageToken(): Task<String>
}