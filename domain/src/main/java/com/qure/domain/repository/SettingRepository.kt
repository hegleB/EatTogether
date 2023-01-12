package com.qure.domain.repository

import com.qure.domain.model.Setting
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

typealias AddSetting = Resource<Boolean, String>

interface SettingRepository {
    suspend fun setSetting(uid: String, setting: Setting): AddSetting
}