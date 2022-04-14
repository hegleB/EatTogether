package com.qure.eattogether2.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class SavedStateViewModel @ViewModelInject constructor(

    @Assisted private val handle : SavedStateHandle
) : ViewModel(){

    private var name = handle.get<String>("name") ?: null
    set(value) {
        handle.set("name", value)
        field = value
    }

    private val _nameForm = MutableLiveData<String>(name)
    val nameState : LiveData<String> = _nameForm

    val nameLiveData : LiveData<String> = handle.getLiveData("name", "")

    fun getName(newName : String) {
        name = newName
        _nameForm.value = name
    }




}