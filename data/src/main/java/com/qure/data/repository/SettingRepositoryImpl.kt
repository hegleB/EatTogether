package com.qure.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.qure.domain.model.Setting
import com.qure.domain.repository.AddSetting
import com.qure.domain.repository.SettingRepository
import com.qure.domain.utils.Constants
import com.qure.domain.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SettingRepository {

    override suspend fun setSetting(uid: String, setting: Setting): AddSetting {
        return try {
            firestore.collection(Constants.SETTING_COLLECTION_PATH)
                .document(uid)
                .set(setting)
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}