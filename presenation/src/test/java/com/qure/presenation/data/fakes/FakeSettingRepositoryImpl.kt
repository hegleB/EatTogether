package com.qure.presenation.data.fakes

import com.qure.domain.model.Setting
import com.qure.domain.repository.AddSetting
import com.qure.domain.repository.SettingRepository
import com.qure.domain.utils.Resource

class FakeSettingRepositoryImpl: SettingRepository {
    override suspend fun setSetting(uid: String, setting: Setting): AddSetting {
        return if (uid.isNullOrBlank()) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }
}