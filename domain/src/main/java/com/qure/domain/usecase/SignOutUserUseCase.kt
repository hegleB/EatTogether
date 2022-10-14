package com.qure.domain.usecase

import com.qure.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun signOutUser() = authRepository.signOutUser()
}