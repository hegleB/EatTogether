package com.qure.domain.usecase

import com.qure.domain.repository.BarcodeRepository
import javax.inject.Inject

class BarcodeUseCase @Inject constructor(
    private val barcodeRepository: BarcodeRepository
) {
    suspend fun checkBarcodeTime(uid: String) = barcodeRepository.checkBarcodeTime(uid)
    suspend fun deleteBarcodeTime(uid: String) = barcodeRepository.deleteBarcodeTime(uid)
    suspend fun deleteBarcodeInfo(uid: String) = barcodeRepository.deleteBarcodeInfo(uid)
    suspend fun getBarcodeTime(uid: String) = barcodeRepository.getBarcodeTime(uid)
    suspend fun setBarcodeTime(uid: String) = barcodeRepository.setBarcodeTime(uid)
    suspend fun setBarcodeInfo(uid: String, randomValue: String) =
        barcodeRepository.setBarcodeInfo(uid, randomValue)
}