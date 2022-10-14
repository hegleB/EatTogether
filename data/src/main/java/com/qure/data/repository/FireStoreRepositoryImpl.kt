package com.qure.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.domain.repository.FireStoreRepository
import com.qure.domain.usecase.GetCurrentUserUseCase
import javax.inject.Inject

class FireStoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : FireStoreRepository {

    private val uid = firebaseAuth.currentUser?.uid

    override suspend fun setFireStoreUser(): DocumentReference {
        return firestore.collection("users").document(uid?:"")
    }

    override suspend fun setFireStoreSetting(): DocumentReference {
        return firestore.collection("setting").document(uid?:"")
    }

    override suspend fun setFireStoreMeeting(): DocumentReference {
        return firestore.collection("meeting").document(uid?:"")
    }

}