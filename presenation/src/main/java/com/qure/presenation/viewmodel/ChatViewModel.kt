package com.qure.presenation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.qure.domain.model.ChatRoom
import com.qure.domain.model.User
import com.qure.domain.repository.AddChatRoom
import com.qure.domain.usecase.ChatUseCase
import com.qure.domain.usecase.UserUseCase
import com.qure.domain.utils.CHATROOMS_COLLECTION_PATH
import com.qure.domain.utils.Resource
import com.qure.presenation.base.BaseViewModel
import com.qure.presenation.utils.FirebaseId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val chatUseCase: ChatUseCase,
) : BaseViewModel() {

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User>
        get() = _user

    private val _otherUser: MutableLiveData<User> = MutableLiveData()

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
        userUseCase.getUser(uid).collect {
            when (it) {
                is Resource.Success -> {
                    if (uid == currentUid.value) _user.value = it.data else _otherUser.value = it.data
                }
            }
        }
    }

    private fun isCurrentUser(uid: String) = uid == currentUid.value

    fun getAllChatRoom() = viewModelScope.launch {
        chatUseCase.getAllChatRoom(currentUid.value ?: "")
            .collect {
                when (it) {
                    is Resource.Success -> _chatRooms.value = it.data
                    is Resource.Loading -> showProgress()
                }
            }
    }

    fun findChatRoom(otherUid: String): ChatRoom =
        _chatRooms.value
            ?.find { it.isContainUid(otherUid) && it.isCorrectOneToOneChatroom() }
            ?: throw IllegalArgumentException("존재하지 않는 채팅방입니다.")

    fun setChatRoom(otherUid: String): ChatRoom {
        val uid = currentUid.value ?: ""
        val chatRoomId = FirebaseId.create(CHATROOMS_COLLECTION_PATH)
        val users = arrayListOf(otherUid, uid)
        val userPhoto = user.value?.userphoto ?: ""
        val otherUserPhoto = _otherUser.value?.userphoto ?: ""
        val photos = mutableMapOf(uid to userPhoto, otherUid to otherUserPhoto)
        val unreadUsers = mutableMapOf(uid to 0, otherUid to 0)
        val chatRoom = ChatRoom(
            roomId = chatRoomId,
            photo = photos,
            userCount = users.size,
            unreadCount = unreadUsers,
            users = users
        )
        addChatRoom(chatRoom)
        return chatRoom
    }

    fun addChatRoom(chatRoom: ChatRoom) = viewModelScope.launch {
        addChatRoom = chatUseCase.setChatRoom(chatRoom)
    }
}
