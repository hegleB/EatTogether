package com.qure.eattogether2.viewmodel

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.Query.*
import com.google.firebase.firestore.ktx.toObject
import com.qure.eattogether2.data.User
import com.qure.eattogether2.paging.PeoplePagingSource
import com.google.firebase.firestore.Query.Direction.ASCENDING
import com.qure.eattogether2.repository.PeopleRepository
import com.qure.eattogether2.repository.PeopleRepository.Companion.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class PeopleViewModel @ViewModelInject constructor(



) : ViewModel() {


    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance().currentUser

    var userimage = MutableLiveData<String>()
    var userName = MutableLiveData<String>()
    var userMsg = MutableLiveData<String>()
    var userid = MutableLiveData<String>()

    var users = MutableLiveData<User>()



    fun getUsers(allUsers :MutableLiveData<MutableList<User>> ) = Pager(
        PagingConfig(
            pageSize = 30
        )
    ) {

        PeoplePagingSource(allUsers)
    }.flow.cachedIn(viewModelScope)


    fun getMyProfileImage(): MutableList<MutableLiveData<String>> {

        val list = mutableListOf<MutableLiveData<String>>()


        db.collection("users").document(auth!!.uid).addSnapshotListener() { result,e ->

            val profile = result!!.toObject<User>()
            users.value = profile!!
            userid.value = profile!!.uid
            userName.value = profile!!.usernm
            userimage.value = profile!!.userphoto
            userMsg.value = profile!!.usermsg

        }

        list.add(userimage)
        list.add(userName)
        list.add(userMsg)

        return list


    }
}