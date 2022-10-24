package com.qure.domain.usecase.profile

import com.qure.domain.repository.BarcodeRepository
import javax.inject.Inject

class SetBarcodeUseCase @Inject constructor(
    private val barcodeRepository: BarcodeRepository
) {
    suspend operator fun invoke(uid : String, randomValue: String) = barcodeRepository.setBarcodeInfo(uid, randomValue)
}