package com.qure.domain.repository

import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

typealias AddMeetingCount = Resource<Boolean, String>
typealias MeetingCountResource = Resource<Int ,String>
typealias UpdateMeetingCount = Resource<Boolean, String>

interface MeetingRepository {
    suspend fun setMeetingCount(uid: String, count: Int): AddMeetingCount
    suspend fun getMeetingCount(uid: String): Flow<MeetingCountResource>
    suspend fun updateMeetingCount(uid: String, count: Int): UpdateMeetingCount
}