package com.qure.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.qure.domain.model.BarcodeScan
import com.qure.domain.repository.MeetingRepository
import com.qure.domain.utils.Constants
import com.qure.domain.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class MeetingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MeetingRepository {

    override suspend fun setMeetingCount(uid: String, count: Int): Flow<Resource<String, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback = firestore.collection(Constants.MEETING_COLLECTION_PATH).document(uid).set(BarcodeScan(count))
                .addOnSuccessListener {
                    this.trySendBlocking(Resource.Success("미팅 설정 성공"))
                }.addOnFailureListener {
                this.trySendBlocking(Resource.Error(it.message))
            }
            awaitClose {
                callback.isCanceled
            }
        }
    }

    override suspend fun getMeetingCount(uid: String): Flow<Resource<Int, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback =
                firestore.collection(Constants.MEETING_COLLECTION_PATH).document(uid).addSnapshotListener { snapshot, e ->

                    if (e == null) {
                        val isExists = snapshot?.exists() ?: false

                        if (isExists) {
                            val meeting = snapshot?.toObject(BarcodeScan::class.java)?.meeting ?: 0
                            this.trySendBlocking(Resource.Success(meeting))
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

    override suspend fun updateMeetingCount(
        uid: String,
        count: Int
    ): Flow<Resource<String, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback = firestore.collection(Constants.MEETING_COLLECTION_PATH).document(uid).update("meeting", count)
                .addOnSuccessListener {
                    this.trySendBlocking(Resource.Success("미팅 설정 성공"))
                }.addOnFailureListener {
                this.trySendBlocking(Resource.Error(it.message))
            }
            awaitClose {
                callback.isCanceled
            }
        }
    }


}