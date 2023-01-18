package com.qure.domain.usecase

import com.facebook.AccessToken
import com.google.firebase.auth.AuthCredential
import com.qure.domain.repository.AuthRepository
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun signWithFacebook(token: AccessToken) =
        authRepository.signInWithFacebook(token).addOnCanceledListener {
        }

    fun signOutUser() = authRepository.signOutUser()

    fun signWithGoogle(credential: AuthCredential) = authRepository.signInWithGoogle(credential)
}