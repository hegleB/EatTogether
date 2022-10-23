package com.qure.domain.usecase

import com.qure.domain.model.Setting
import com.qure.domain.repository.SettingRepository
import javax.inject.Inject

class SetSettingUseCase @Inject constructor(
    private val settingRepository: SettingRepository
) {
    suspend operator fun invoke(uid: String, setting : Setting) = settingRepository.setSetting(uid, setting)
}