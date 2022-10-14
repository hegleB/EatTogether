package com.qure.domain.usecase

import com.qure.domain.repository.FireStoreRepository
import javax.inject.Inject

class UseFireStoreMeetingUseCase @Inject constructor(
    private val fireStoreRepository: FireStoreRepository
) {
    suspend fun useFireStoreMeeting() = fireStoreRepository.useFireStoreMeeting()
}