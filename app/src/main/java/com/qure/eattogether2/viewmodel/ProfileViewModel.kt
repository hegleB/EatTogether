package com.qure.eattogether2.viewmodel

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.eattogether2.data.User
import com.qure.eattogether2.repository.AuthRepository

class ProfileViewModel @ViewModelInject constructor(

) : ViewModel() {

    var isEdit = MutableLiveData<Boolean>()

    var isCount = MutableLiveData<Boolean>()

    var user = MutableLiveData<User>()

    var profileImage = MutableLiveData<String>()

    var isChangeImage = MutableLiveData<Boolean>()


}