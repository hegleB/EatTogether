package com.qure.domain.repository

import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface BarcodeRepository {

    suspend fun getBarcodeTime(uid: String): Flow<Resource<Long, String>>
    suspend fun setBarcodeTime(uid: String): Flow<Resource<String, String>>
    suspend fun checkBarcodeTime(uid: String): Flow<Resource<Boolean, String>>
    suspend fun getBarcodeInfo(uid: String): Flow<Resource<String, String>>
    suspend fun setBarcodeInfo(uid: String, randomValue: String): Flow<Resource<String, String>>
    suspend fun deleteBarcodeInfo(uid: String): Flow<Resource<String, String>>
    suspend fun deleteBarcodeTime(uid: String): Flow<Resource<String, String>>
}