package com.qure.presenation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val auth: FirebaseAuth,
) : ViewModel() {

    var isNotify = MutableLiveData<Boolean>()

    fun logout() {
        auth.signOut()
    }
}