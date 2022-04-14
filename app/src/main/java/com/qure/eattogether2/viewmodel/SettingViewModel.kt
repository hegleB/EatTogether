package com.qure.eattogether2.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.qure.eattogether2.prefs.UserPrefrence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingViewModel @ViewModelInject constructor(
    private val auth : FirebaseAuth,
    private val prefs : UserPrefrence
) : ViewModel() {

    var isNotify = MutableLiveData<Boolean>()

    fun logout() {
        auth.signOut()
        viewModelScope.launch(Dispatchers.IO) {
            prefs.setLoginStatus(false)
        }
    }
}