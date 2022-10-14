package com.qure.domain.usecase

import com.qure.domain.repository.FireStoreRepository
import javax.inject.Inject

class UseFireStoreSettingUseCase @Inject constructor(
    private val fireStoreRepository: FireStoreRepository
) {
    suspend fun useFireStoreSetting() = fireStoreRepository.useFireStoreSetting()
}