package com.qure.domain.repository

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow


interface AuthRepository {

    fun signInWithGoogle(credential: AuthCredential): Task<AuthResult>
    fun signInWithFacebook(token: AccessToken): Task<AuthResult>
    fun getCurrentUser(): FirebaseUser?
    suspend fun isJoin(user: FirebaseUser): Flow<Resource<Boolean, String>>
    fun signOutUser()
    suspend fun geMessageToken(): Task<String>
}