package com.qure.presenation.data.fakes

import com.qure.domain.model.User
import com.qure.domain.repository.*
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FakeBarcodeRepositoryImpl : BarcodeRepository {

    private val user = listOf(
        User(
            "test123@gmail.com",
            "abcd1234",
            "user",
            "token"
        )
    )

    override suspend fun getBarcodeTime(uid: String): Flow<BarcodeTimeResource> {
        return callbackFlow {
            when {
                uid.isNullOrBlank() || uid.length <= 0 -> Resource.Error("error")
                else -> Resource.Success(Resource.Success(0L))
            }
        }
    }

    override suspend fun setBarcodeTime(uid: String): AddBarcodeTime {
        return if (uid.isNullOrBlank()) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }

    override suspend fun checkBarcodeTime(uid: String): CheckBarcodeTime {
        return if (uid.isNullOrBlank()) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }

    override suspend fun getBarcodeInfo(uid: String): Flow<BarcodeInfoResource> {
        return callbackFlow {
            if (uid.isNullOrBlank()) {
                Resource.Success(false)
            } else {
                Resource.Success(true)
            }
        }
    }

    override suspend fun setBarcodeInfo(uid: String, randomValue: String): AddBarcodeInfo {
        return if (uid.isNullOrBlank()) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }

    override suspend fun deleteBarcodeInfo(uid: String): DeleteBarcodeInfo {
        return if (uid.isNullOrBlank()) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }

    override suspend fun deleteBarcodeTime(uid: String): DeleteBarcodeTime {
        return if (uid.isNullOrBlank()) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }
}