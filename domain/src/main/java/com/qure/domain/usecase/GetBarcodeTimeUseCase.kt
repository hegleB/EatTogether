package com.qure.domain.usecase

import com.qure.domain.repository.BarcodeRepository
import javax.inject.Inject

class GetBarcodeTimeUseCase @Inject constructor(
    private val barcodeRepository: BarcodeRepository
){
    suspend operator fun invoke(uid: String) = barcodeRepository.getBarcodeTime(uid)
}