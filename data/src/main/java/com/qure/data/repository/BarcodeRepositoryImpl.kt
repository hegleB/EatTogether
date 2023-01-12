package com.qure.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.qure.domain.model.Barcode
import com.qure.domain.model.BarcodeTime
import com.qure.domain.repository.BarcodeRepository
import com.qure.domain.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class BarcodeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BarcodeRepository {

    override suspend fun getBarcodeTime(uid: String): Flow<Resource<Long, String>> {
        return callbackFlow {
            val callback = firestore.collection("barcode_time").document(uid)
                .addSnapshotListener { snapshot, e ->
                    if (e == null) {
                        val isExists = snapshot?.exists() ?: false

                        if (isExists) {
                            val barcodeTime = snapshot?.toObject(BarcodeTime::class.java)!!
                            this.trySendBlocking(Resource.Success(barcodeTime.barcodetime))
                        } else {
                            this.trySendBlocking(Resource.Empty("데이터가 없습니다."))
                        }
                    } else {
                        this.trySendBlocking(Resource.Error(e.message))
                    }

                }
            awaitClose {
                callback.remove()
            }
        }
    }

    override suspend fun setBarcodeTime(uid: String): Flow<Resource<String, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val now = System.currentTimeMillis() + 3 * 3600000
            val callback = firestore.collection("barcode_time").document(uid).set(BarcodeTime(now))
                .addOnSuccessListener {
                    this.trySendBlocking(Resource.Success("바코드 시간 생성 성공"))
                }.addOnFailureListener {
                this.trySendBlocking(Resource.Error(it.message))
            }
            awaitClose {
                callback.isCanceled
            }
        }
    }

    override suspend fun checkBarcodeTime(uid: String): Flow<Resource<Boolean, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback = firestore.collection("barcode_time").document(uid)
                .addSnapshotListener { snapshot, e ->

                    if (e == null) {
                        val isExists = snapshot?.exists() ?: false
                        this.trySendBlocking(Resource.Success(isExists))
                    } else {
                        this.trySendBlocking(Resource.Error(e.message))
                    }
                }
            awaitClose {
                callback.remove()
            }
        }
    }

    override suspend fun getBarcodeInfo(uid: String): Flow<Resource<String, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback =
                firestore.collection("barcode").document(uid).addSnapshotListener { snapshot, e ->
                    if (e == null) {
                        val isExists = snapshot?.exists() ?: false

                        if (isExists) {
                            val barcode = snapshot?.toObject(Barcode::class.java)!!
                            this.trySendBlocking(Resource.Success(barcode.barcode))
                        } else {
                            this.trySendBlocking(Resource.Empty("데이터가 없습니다."))
                        }
                    } else {
                        this.trySendBlocking(Resource.Error(e.message))
                    }
                }
            awaitClose {
                callback.remove()
            }
        }
    }

    override suspend fun setBarcodeInfo(
        uid: String,
        randomValue: String
    ): Flow<Resource<String, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback = firestore.collection("barcode").document(uid).set(Barcode(randomValue))
                .addOnSuccessListener {
                    this.trySendBlocking(Resource.Success("바코드 생성 성공"))

                }.addOnFailureListener {
                this.trySendBlocking(Resource.Error(it.message))
            }
            awaitClose {
                callback.isCanceled
            }
        }


    }

    override suspend fun deleteBarcodeInfo(uid: String): Flow<Resource<String, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback =
                firestore.collection("barcode").document(uid).delete().addOnSuccessListener {
                    this.trySendBlocking(Resource.Success("바코드 삭제 성공"))
                }.addOnFailureListener {
                    this.trySendBlocking(Resource.Success("바코드 삭제 실패"))
                }
            awaitClose {
                callback.isCanceled
            }

        }
    }

    override suspend fun deleteBarcodeTime(uid: String): Flow<Resource<String, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback =
                firestore.collection("barcode_time").document(uid).delete().addOnSuccessListener {
                    this.trySendBlocking(Resource.Success("바코드 삭제 성공"))
                }.addOnFailureListener {
                    this.trySendBlocking(Resource.Success("바코드 삭제 실패"))
                }
            awaitClose {
                callback.isCanceled
            }
        }
    }


}