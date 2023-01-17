package com.qure.domain.usecase.auth

import com.google.firebase.auth.AuthCredential
import com.qure.domain.repository.AuthRepository
import javax.inject.Inject

class SignWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun signWithGoogle(credential: AuthCredential) = authRepository.signInWithGoogle(credential)
}