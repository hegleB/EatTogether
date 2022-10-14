package com.qure.domain.usecase

import com.qure.domain.repository.FireStoreRepository
import javax.inject.Inject

class SetFireStoreSettingUseCase @Inject constructor(
    private val fireStoreRepository: FireStoreRepository
) {
    suspend fun setFireStoreSetting() = fireStoreRepository.setFireStoreSetting()
}