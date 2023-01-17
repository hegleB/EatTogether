package com.qure.domain.repository

import com.qure.domain.model.Setting
import com.qure.domain.utils.Resource

typealias AddSetting = Resource<Boolean, String>

interface SettingRepository {
    suspend fun setSetting(uid: String, setting: Setting): AddSetting
}