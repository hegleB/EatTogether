package com.qure.domain.repository

import com.google.firebase.firestore.DocumentReference

interface FireStoreRepository {

    suspend fun useFireStoreUser() : DocumentReference
    suspend fun useFireStoreSetting() : DocumentReference
    suspend fun useFireStoreMeeting() : DocumentReference
}