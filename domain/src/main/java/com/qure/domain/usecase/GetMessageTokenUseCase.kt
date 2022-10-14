package com.qure.domain.usecase

import com.qure.domain.repository.AuthRepository
import javax.inject.Inject

class GetMessageTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun getMessageToken() = authRepository.geMessageToken()
}