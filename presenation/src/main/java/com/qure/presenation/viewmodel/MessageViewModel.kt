package com.qure.presenation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.qure.domain.model.ChatComment
import com.qure.domain.model.ChatRoom
import com.qure.domain.model.Comments
import com.qure.domain.model.User
import com.qure.domain.usecase.people.GetUserInfoUseCase
import com.qure.domain.utils.Resource
import com.qure.presenation.Event
import com.qure.presenation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    val firebaseAuth: FirebaseAuth,
    val getUserInfoUseCase: GetUserInfoUseCase
) : BaseViewModel() {

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User>
        get() = _user

    val editTextMessage: MutableLiveData<String> = MutableLiveData("")

    private val _buttonSendMessage: MutableLiveData<Event<Unit>> = MutableLiveData()
    val buttonSendMessage: LiveData<Event<Unit>>
        get() = _buttonSendMessage

    private val _chatroom: MutableLiveData<ChatRoom> = MutableLiveData()
    val chatroom: LiveData<ChatRoom>
        get() = _chatroom

    fun getUserInfo() = viewModelScope.launch {
        getUserInfoUseCase(user.value?.uid ?: "")
            .collect {
                when (it) {
                    is Resource.Success -> {
                        _user.value = it.data
                    }
                    is Resource.Loading -> showProgress()
                }
            }
    }

    fun writeMessage(message: String) = viewModelScope.launch {
        val uid = user.value?.uid ?: ""
        val chatComment = ChatComment(
            chatroom.value?.roomId ?: "",
            user.value?.userphoto ?: "",
            uid,
            user.value?.usernm ?: "",
            message,
            "1",
            System.currentTimeMillis().toString(),
            mutableMapOf(uid to true)
        )
    }

    fun sendMessage(messageText: String) {
        _buttonSendMessage.value = Event(Unit)
        writeMessage(messageText)
    }

    fun getChatRoom(chatroom: ChatRoom) {
        _chatroom.value = chatroom
    }
}