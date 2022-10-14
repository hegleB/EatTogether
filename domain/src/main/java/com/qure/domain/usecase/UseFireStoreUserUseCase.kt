package com.qure.domain.usecase

import com.qure.domain.repository.FireStoreRepository
import javax.inject.Inject

class UseFireStoreUserUseCase @Inject constructor(
    private val fireStoreRepository: FireStoreRepository
) {
    suspend fun useFireStoreUser() = fireStoreRepository.useFireStoreUser()
}