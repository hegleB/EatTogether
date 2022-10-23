package com.qure.domain.usecase

import com.qure.domain.repository.BarcodeRepository
import javax.inject.Inject

class CheckBarcodeTimeUseCase @Inject constructor(
    private val barcodeRepository: BarcodeRepository
){
    suspend operator fun invoke(uid : String) = barcodeRepository.checkBarcodeTime(uid)
}