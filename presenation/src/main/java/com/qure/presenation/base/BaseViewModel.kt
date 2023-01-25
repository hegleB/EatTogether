package com.qure.presenation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _currentUid: MutableLiveData<String> = MutableLiveData()
    val currentUid: LiveData<String> get() = _currentUid
    fun getCurrentUid(currentUid: String) {
        _currentUid.value = currentUid
    }
    fun showProgress() {
        _isLoading.value = true
    }

    fun hideProgress() {
        _isLoading.value = false
    }
}
