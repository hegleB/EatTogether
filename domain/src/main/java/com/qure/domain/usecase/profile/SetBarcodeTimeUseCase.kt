package com.qure.domain.usecase.profile

import com.qure.domain.repository.BarcodeRepository
import javax.inject.Inject

class SetBarcodeTimeUseCase @Inject constructor(
    private val barcodeRepository: BarcodeRepository
) {
    suspend operator fun invoke(uid: String) = barcodeRepository.setBarcodeTime(uid)
}