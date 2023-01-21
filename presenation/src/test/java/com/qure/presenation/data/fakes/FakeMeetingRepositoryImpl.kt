package com.qure.presenation.data.fakes

import com.qure.domain.repository.AddMeetingCount
import com.qure.domain.repository.MeetingCountResource
import com.qure.domain.repository.MeetingRepository
import com.qure.domain.repository.UpdateMeetingCount
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FakeMeetingRepositoryImpl: MeetingRepository {
    override suspend fun setMeetingCount(uid: String, count: Int): AddMeetingCount {
        return if (uid.isNullOrBlank()) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }

    override suspend fun getMeetingCount(uid: String): Flow<MeetingCountResource> {
        return callbackFlow {
            if (uid.isNullOrBlank()) {
                Resource.Success(false)
            } else {
                Resource.Success(true)
            }
        }
    }

    override suspend fun updateMeetingCount(uid: String, count: Int): UpdateMeetingCount {
        return if (uid.isNullOrBlank()) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }
}