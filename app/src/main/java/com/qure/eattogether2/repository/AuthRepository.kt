package com.qure.eattogether2.repository

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.qure.eattogether2.data.remote.FirebaseSource
import javax.inject.Inject

class AuthRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

    fun signInWithGoogle(acct: GoogleSignInAccount) =
        firebaseSource.signInWithGoogle(acct)

    fun signInWithFaceBook(token : AccessToken) =
        firebaseSource.signInWithFaceBook(token)

}