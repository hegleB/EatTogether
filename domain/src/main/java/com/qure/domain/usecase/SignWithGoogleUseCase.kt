package com.qure.domain.usecase

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.qure.domain.repository.AuthRepository
import javax.inject.Inject

class SignWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun signWithGoogle(account : GoogleSignInAccount) = authRepository.signInWithGoogle(account)
}