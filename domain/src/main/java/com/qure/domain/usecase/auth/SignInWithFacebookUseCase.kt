package com.qure.domain.usecase.auth

import com.facebook.AccessToken
import com.qure.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithFacebookUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun signWithFacebook(token: AccessToken) =
        authRepository.signInWithFacebook(token).addOnCanceledListener {
        }
}