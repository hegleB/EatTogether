package com.qure.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.qure.domain.model.Barcode
import com.qure.domain.model.BarcodeTime
import com.qure.domain.repository.*
import com.qure.domain.utils.BARCODE_COLLECTION_PATH
import com.qure.domain.utils.BARCODE_TIME_COLLECTION_PATH
import com.qure.domain.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BarcodeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BarcodeRepository {

    override suspend fun getBarcodeTime(uid: String) = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collection(BARCODE_TIME_COLLECTION_PATH).document(uid)
            .addSnapshotListener { snapshot, e ->
                val barcodeResource = if (snapshot != null) {
                    val barcodeTime = snapshot.toObject(BarcodeTime::class.java)
                    Resource.Success(barcodeTime?.barcodetime ?: 0L)
                } else {
                    Resource.Error(e?.message)
                }
                trySend(barcodeResource)
            }
        awaitClose {
            callback.remove()
        }
    }


    override suspend fun setBarcodeTime(uid: String): AddBarcodeTime {
        return try {
            val now = System.currentTimeMillis() + MAX_BARCODE_TIME
            firestore.collection(BARCODE_TIME_COLLECTION_PATH)
                .document(uid)
                .set(BarcodeTime(now))
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }


    override suspend fun checkBarcodeTime(uid: String): CheckBarcodeTime {
        return try {
            firestore.collection(BARCODE_TIME_COLLECTION_PATH)
                .document(uid)
                .get()
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun getBarcodeInfo(uid: String) = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collection(BARCODE_COLLECTION_PATH)
            .document(uid)
            .addSnapshotListener { snapshot, e ->
                val barcodeInfoResource = if (snapshot != null) {
                    val barcode = snapshot.toObject(Barcode::class.java)
                    Resource.Success(barcode?.barcode ?: "")
                } else {
                    Resource.Error(e?.message)
                }
                trySend(barcodeInfoResource)
            }
        awaitClose {
            callback.remove()
        }
    }

    override suspend fun setBarcodeInfo(uid: String, randomValue: String): AddBarcodeInfo {
        return try {
            firestore.collection(BARCODE_COLLECTION_PATH)
                .document(uid)
                .set(Barcode(randomValue))
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteBarcodeInfo(uid: String): DeleteBarcodeInfo {
        return try {
            firestore.collection(BARCODE_COLLECTION_PATH)
                .document(uid)
                .delete()
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteBarcodeTime(uid: String): DeleteBarcodeTime {
        return try {
            firestore.collection(BARCODE_COLLECTION_PATH)
                .document(uid)
                .delete()
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    companion object {
        const val MAX_BARCODE_TIME = 3 * 3600000
    }
}