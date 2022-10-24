package com.qure.domain.usecase.profile

import com.qure.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun getCurrentUser() = authRepository.getCurrentUser()
}