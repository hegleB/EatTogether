package com.qure.presenation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.domain.model.ChatRoom
import com.qure.domain.model.User
import com.qure.domain.repository.AddChatRoom
import com.qure.domain.usecase.chat.GetAllChatRoomUseCase
import com.qure.domain.usecase.chat.SetChatRoomUseCase
import com.qure.domain.usecase.people.GetUserInfoUseCase
import com.qure.domain.utils.Resource
import com.qure.presenation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val getAllChatRoomUseCase: GetAllChatRoomUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val setChatRoomUseCase: SetChatRoomUseCase
) : BaseViewModel() {

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User>
        get() = _user

    private val _otherUser: MutableLiveData<User> = MutableLiveData()
    val otherUser: LiveData<User>
        get() = _otherUser

    val curruntUid = firebaseAuth.currentUser?.uid ?: ""
    val chatroomId = firestore.collection("chatrooms").document().id

    private val _chatRooms: MutableLiveData<List<ChatRoom>> = MutableLiveData(emptyList())
    val chatRooms: LiveData<List<ChatRoom>>
        get() = _chatRooms

    private val _chatRoom: MutableLiveData<ChatRoom> = MutableLiveData(ChatRoom())
    val chatRoom: LiveData<ChatRoom>
        get() = _chatRoom

    var addChatRoom by mutableStateOf<AddChatRoom>(Resource.Success(false))
        private set

    fun getUserInfo(uid: String) {
        if (isCurrentUser(uid)) {
            getUser(uid)
            return
        }
        getUser(uid)
    }

    private fun getUser(uid: String) = viewModelScope.launch {
        getUserInfoUseCase(uid).collect {
            when (it) {
                is Resource.Success -> _user.value = it.data
            }
        }
    }

    private fun isCurrentUser(uid: String) = uid == curruntUid

    fun getAllChatRoom() = viewModelScope.launch {
        getAllChatRoomUseCase(curruntUid)
            .collect {
                when (it) {
                    is Resource.Success -> {
                        _chatRooms.value = it.data
                    }
                    is Resource.Loading -> showProgress()
                }
            }

    }

    fun findChatRoom(otherUid: String): ChatRoom =
        _chatRooms.value
            ?.find { it.isContainUid(otherUid) && it.isCorrectOneToOneChatroom() }
            ?: throw IllegalArgumentException("존재하지 않는 채팅방입니다.")

    fun setChatRoom(otherUid: String) {
        val users = arrayListOf(otherUid, curruntUid)
        val userPhoto = user.value?.userphoto ?: ""
        val otherUserPhoto = otherUser.value?.userphoto ?: ""
        val chatRoom = ChatRoom(
            false,
            chatroomId,
            "",
            mutableMapOf(
                curruntUid to userPhoto,
                otherUid to otherUserPhoto
            ),
            "",
            "",
            users.size,
            mutableMapOf(curruntUid to 0, otherUid to 0),
            users
        )
        _chatRoom.value = ChatRoom()
        addChatRoom(chatRoom)
    }

    fun addChatRoom(chatRoom: ChatRoom) = viewModelScope.launch {
        addChatRoom = Resource.Loading()
        addChatRoom = setChatRoomUseCase(chatRoom)
    }
}