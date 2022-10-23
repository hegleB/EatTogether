package com.qure.domain.repository

import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface MeetingRepository {

    suspend fun setMeetingCount(uid : String, count : Int) : Flow<Resource<String,String>>
    suspend fun getMeetingCount(uid : String) : Flow<Resource<Int, String>>
    suspend fun updateMeetingCount(uid : String, count: Int) : Flow<Resource<String, String>>
}