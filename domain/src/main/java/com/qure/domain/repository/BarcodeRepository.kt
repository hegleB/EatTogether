package com.qure.domain.repository

import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

typealias BarcodeTimeResource = Resource<Long, String>
typealias AddBarcodeTime = Resource<Boolean, String>
typealias CheckBarcodeTime = Resource<Boolean, String>
typealias BarcodeInfoResource = Resource<String, String>
typealias AddBarcodeInfo = Resource<Boolean, String>
typealias DeleteBarcodeInfo = Resource<Boolean, String>
typealias DeleteBarcodeTime = Resource<Boolean, String>

interface BarcodeRepository {
    suspend fun getBarcodeTime(uid: String): Flow<BarcodeTimeResource>
    suspend fun setBarcodeTime(uid: String): AddBarcodeTime
    suspend fun checkBarcodeTime(uid: String): CheckBarcodeTime
    suspend fun getBarcodeInfo(uid: String): Flow<BarcodeInfoResource>
    suspend fun setBarcodeInfo(uid: String, randomValue: String): AddBarcodeInfo
    suspend fun deleteBarcodeInfo(uid: String): DeleteBarcodeInfo
    suspend fun deleteBarcodeTime(uid: String): DeleteBarcodeTime
}