package com.qure.eattogether2.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.qure.eattogether2.data.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PeopleRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    fun getAllUser() : Flow<List<User>>{

       return callbackFlow {
           val callback = firestore.collection("users").orderBy("usernm").addSnapshotListener{ snapshots,e ->
               if(e!=null){
                   close(e)
               } else {

                   offer(snapshots!!.toObjects(User::class.java))
               }
           }
           awaitClose{
               callback.remove()
           }
       }
    }
    companion object {
        const val TAG = "People Repository"
    }

}