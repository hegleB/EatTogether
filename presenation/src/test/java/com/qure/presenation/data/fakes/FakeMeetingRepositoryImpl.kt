package com.qure.presenation.data.fakes

import com.qure.domain.model.BarcodeScan
import com.qure.domain.repository.AddMeetingCount
import com.qure.domain.repository.MeetingCountResource
import com.qure.domain.repository.MeetingRepository
import com.qure.domain.repository.UpdateMeetingCount
import com.qure.domain.utils.Resource
import com.qure.presenation.data.utils.TestDataUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf

class FakeMeetingRepositoryImpl: MeetingRepository {

    private val meeting = TestDataUtils.meeting

    override suspend fun setMeetingCount(uid: String, count: Int): AddMeetingCount {
        return if (uid.isNullOrBlank()) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }

    override suspend fun getMeetingCount(uid: String): Flow<MeetingCountResource> {
        val meetingCount = meeting[uid] ?: 0
        return flowOf(Resource.Success(meetingCount))
    }

    override suspend fun updateMeetingCount(uid: String, count: Int): UpdateMeetingCount {
        return if (uid.isNullOrBlank()) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }
}