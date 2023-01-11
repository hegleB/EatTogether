package com.qure.presenation.viewmodel

import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.qure.domain.model.*
import com.qure.domain.usecase.chat.*
import com.qure.domain.usecase.people.GetUserInfoUseCase
import com.qure.domain.utils.Resource
import com.qure.presenation.Event
import com.qure.presenation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    val firestore: FirebaseFirestore,
    val firebaseAuth: FirebaseAuth,
    val getAllMessageUsecase: GetAllMessageUseCase,
    val setChatMessageUsecase: SetChatMessageUsecase,
    val getUserInfoUseCase: GetUserInfoUseCase,
    val updateChatRoomUseCase: UpdateChatRoomUseCase,
    val updateChatUseCase: UpdateChatUseCase
) : BaseViewModel() {

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User>
        get() = _user

    private val currentUser = firebaseAuth.currentUser?.uid ?: ""

    val editTextMessage: MutableLiveData<String> = MutableLiveData("")

    private val _buttonSendMessage: MutableLiveData<Event<Unit>> = MutableLiveData()
    val buttonSendMessage: LiveData<Event<Unit>>
        get() = _buttonSendMessage

    private val _messages: MutableLiveData<List<ChatMessage>> = MutableLiveData()
    val messages: LiveData<List<ChatMessage>>
        get() = _messages

    private val _chatroom: MutableLiveData<ChatRoom> = MutableLiveData()
    val chatroom: LiveData<ChatRoom>
        get() = _chatroom

    fun getUserInfo() = viewModelScope.launch {
        getUserInfoUseCase(currentUser).collect {
            when (it) {
                is Resource.Success -> _user.value = it.data
                is Resource.Empty -> hideProgress()
            }
        }
    }

    fun getMessage(roomId: String) = viewModelScope.launch {
        getAllMessageUsecase(roomId).collect {
            when (it) {
                is Resource.Success -> _messages.value = it.data
                is Resource.Empty -> _messages.value = emptyList()
            }
        }
    }

    fun readMessage() {
        val roomId = _chatroom.value?.roomId ?: ""
        firestore.collection("chat").whereEqualTo("roomId", roomId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null) {
                    val documents = findMydocument(snapshot.documents)
                    for (document in documents) {
                        val data = document.toObject(ChatMessage::class.java)?.readUsers ?: mutableMapOf()
                        data.put(currentUser, true)
                        document.reference.update("readUsers", data)
                        val unreadCount = _chatroom.value?.unreadCount ?: mutableMapOf()
                        unreadCount.put(currentUser, 0)
                        viewModelScope.launch {
                            updateChatRoomUseCase(roomId, unreadCount).collect {
                                when (it) {
                                    is Resource.Success -> hideProgress()
                                }
                            }
                        }
                    }
                }

            }
    }

    fun findMydocument(documents: List<DocumentSnapshot>): List<DocumentSnapshot> =
        documents.filter { it.toObject(ChatMessage::class.java)?.isNotcontainUid(currentUser) ?: false }

    fun writeMessage(editText: String) {
        _buttonSendMessage.value = Event(Unit)
        editTextMessage.value = ""
        println(currentUser)
        val chatMessage = ChatMessage(
            _chatroom.value?.roomId ?: "",
            _user.value?.userphoto ?: "",
            currentUser,
            _user.value?.usernm ?: "",
            editText,
            "1",
            System.currentTimeMillis().toString(),
            mutableMapOf(currentUser to true)
        )
        println(chatMessage)
        setChatMessage(chatMessage)
    }

    fun setChatMessage(chatMessage: ChatMessage) = viewModelScope.launch {
        setChatMessageUsecase(chatMessage).collect {
            when (it) {
                is Resource.Success -> hideProgress()
                is Resource.Empty -> hideProgress()
            }
        }
    }

    fun getChatRoom(chatroom: ChatRoom) {
        _chatroom.value = chatroom
    }
}