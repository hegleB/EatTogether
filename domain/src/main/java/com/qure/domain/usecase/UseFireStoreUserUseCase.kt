package com.qure.domain.usecase

import com.qure.domain.repository.FireStoreRepository
import javax.inject.Inject

class SetFireStoreUserUseCase @Inject constructor(
    private val fireStoreRepository: FireStoreRepository
) {
    suspend fun setFireStoreUser() = fireStoreRepository.setFireStoreUser()
}