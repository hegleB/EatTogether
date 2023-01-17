package com.qure.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.qure.domain.model.Setting
import com.qure.domain.repository.AddSetting
import com.qure.domain.repository.SettingRepository
import com.qure.domain.utils.Resource
import com.qure.domain.utils.SETTING_COLLECTION_PATH
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SettingRepository {

    override suspend fun setSetting(uid: String, setting: Setting): AddSetting {
        return try {
            firestore.collection(SETTING_COLLECTION_PATH)
                .document(uid)
                .set(setting)
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}