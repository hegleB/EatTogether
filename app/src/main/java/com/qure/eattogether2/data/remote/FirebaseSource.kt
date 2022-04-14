package com.qure.eattogether2.data.remote

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject

class FirebaseSource @Inject constructor(val firebaseAuth: FirebaseAuth) {

    fun signInWithGoogle(acct : GoogleSignInAccount) = firebaseAuth.signInWithCredential(
        GoogleAuthProvider.getCredential(acct.idToken, null)
    )

    fun signInWithFaceBook(token: AccessToken) = firebaseAuth.signInWithCredential(
        FacebookAuthProvider.getCredential(token.token)
    )

}