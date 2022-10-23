package com.qure.domain.repository

import com.qure.domain.model.Setting
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface SettingRepository {

    suspend fun setSetting(uid : String, setting : Setting) : Flow<Resource<String, String>>
}