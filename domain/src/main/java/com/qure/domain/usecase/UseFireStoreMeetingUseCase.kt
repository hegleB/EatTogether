package com.qure.domain.usecase

import com.qure.domain.repository.FireStoreRepository
import javax.inject.Inject

class SetFireStoreMeetingUseCase @Inject constructor(
    private val fireStoreRepository: FireStoreRepository
) {
    suspend fun setFireStoreMeeting() = fireStoreRepository.setFireStoreMeeting()
}