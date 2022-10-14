package com.qure.domain.repository

import com.google.firebase.firestore.DocumentReference

interface FireStoreRepository {

    suspend fun setFireStoreUser() : DocumentReference
    suspend fun setFireStoreSetting() : DocumentReference
    suspend fun setFireStoreMeeting() : DocumentReference
}