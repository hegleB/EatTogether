package com.qure.eattogether2.viewmodel

import android.net.Uri
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.qure.eattogether2.data.User
import com.qure.eattogether2.repository.AuthRepository
import com.qure.eattogether2.utils.Resource


class AuthViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {


    private val googleUserLiveData = MutableLiveData<Resource<FirebaseUser>>()
    private val facebookUserLiveData = MutableLiveData<Resource<FirebaseUser>>()

    var isUser = MutableLiveData<Boolean>()
    var profileUri = MutableLiveData<Uri>()

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount): LiveData<Resource<FirebaseUser>> {
        authRepository.signInWithGoogle(account)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    isJoin(user!!)

                    googleUserLiveData.postValue(Resource.success(user!!))
                } else {
                    googleUserLiveData.postValue(
                        Resource.error(task.exception?.message.toString(), null)
                    )
                }
            }

        return googleUserLiveData

    }

    fun firebaseAuthWithFaceBook(token: AccessToken): LiveData<Resource<FirebaseUser>> {

        authRepository.signInWithFaceBook(token)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    isJoin(user!!)


                    facebookUserLiveData.postValue(Resource.success(user!!))

                } else {

                }
            }

        return facebookUserLiveData

    }

    fun isJoin(user: FirebaseUser) {
        firestore.collection("users").document(user.uid).addSnapshotListener { snapshot, e ->

            if (e != null) {

            }
            val data = snapshot?.data


            if (data!=null) {
                isUser.value = true
            } else {
                isUser.value = false
            }

        }

    }

}
