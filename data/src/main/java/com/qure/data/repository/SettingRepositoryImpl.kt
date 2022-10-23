package com.qure.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.qure.domain.model.Setting
import com.qure.domain.repository.SettingRepository
import com.qure.domain.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SettingRepository{

    override suspend fun setSetting(uid : String, setting: Setting): Flow<Resource<String, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback = firestore.collection("setting").document(uid).set(setting).addOnSuccessListener {
                this.trySendBlocking(Resource.Success("설정 성공"))
            }.addOnFailureListener {
                this.trySendBlocking(Resource.Error(it.message))
            }
            awaitClose {
                callback.isCanceled
            }
        }

    }


}