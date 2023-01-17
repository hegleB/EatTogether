package com.qure.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.qure.domain.model.BarcodeScan
import com.qure.domain.repository.AddMeetingCount
import com.qure.domain.repository.MeetingRepository
import com.qure.domain.repository.UpdateMeetingCount
import com.qure.domain.utils.MEETING_COLLECTION_PATH
import com.qure.domain.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MeetingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MeetingRepository {

    override suspend fun setMeetingCount(uid: String, count: Int): AddMeetingCount {
        return try {
            firestore.collection(MEETING_COLLECTION_PATH)
                .document(uid).set(BarcodeScan(count))
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun getMeetingCount(uid: String) = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collection(MEETING_COLLECTION_PATH)
            .document(uid)
            .addSnapshotListener { snapshot, e ->
                val meetingCountResource = if (snapshot != null) {
                    val meeting = snapshot.toObject(BarcodeScan::class.java)?.meeting ?: 0
                    Resource.Success(meeting)
                } else {
                    Resource.Error(e?.message)
                }
                trySend(meetingCountResource)
            }
        awaitClose {
            callback.remove()
        }
    }

    override suspend fun updateMeetingCount(uid: String, count: Int): UpdateMeetingCount {
        return try {
            firestore.collection(MEETING_COLLECTION_PATH).document(uid)
                .update("meeting", count)
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}