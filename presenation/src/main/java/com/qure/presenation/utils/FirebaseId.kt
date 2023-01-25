package com.qure.presenation.utils

import com.google.firebase.firestore.FirebaseFirestore

class FirebaseId {
    companion object {
        fun create(collectionPath: String): String {
            return FirebaseFirestore.getInstance().collection(collectionPath).document().id
        }
    }
}