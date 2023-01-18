package com.qure.presenation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.domain.model.ChatMessage
import com.qure.domain.model.ChatRoom
import com.qure.domain.model.User
import com.qure.domain.repository.AddChatMessage
import com.qure.domain.repository.UpdateChatRoom
import com.qure.domain.usecase.ChatUseCase
import com.qure.domain.usecase.UserUseCase
import com.qure.domain.utils.*
import com.qure.presenation.Event
import com.qure.presenation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val userUseCase: UserUseCase,
    private val chatUseCase: ChatUseCase,
) : BaseViewModel() {

    private val _user: MutableLiveData<User> = MutableLiveData(User())
    val user: LiveData<User>
        get() = _user

    private val currentUser = firebaseAuth.currentUser?.uid ?: ""

    val editTextMessage: MutableLiveData<String> = MutableLiveData("")

    private val _buttonSendMessage: MutableLiveData<Event<Unit>> = MutableLiveData()
    val buttonSendMessage: LiveData<Event<Unit>>
        get() = _buttonSendMessage

    private val _buttonImageSelection: MutableLiveData<Event<Unit>> = MutableLiveData()
    val buttonImageSelection: LiveData<Event<Unit>>
        get() = _buttonImageSelection

    private val _buttonAddUsers: MutableLiveData<Event<Unit>> = MutableLiveData()
    val buttonAddUsers: LiveData<Event<Unit>>
        get() = _buttonAddUsers

    private val _messages: MutableLiveData<List<ChatMessage>> = MutableLiveData(emptyList())
    val messages: LiveData<List<ChatMessage>>
        get() = _messages

    private val _chatroom: MutableLiveData<ChatRoom> = MutableLiveData()
    val chatroom: LiveData<ChatRoom>
        get() = _chatroom

    private val _selectedUsers: MutableLiveData<List<User>> = MutableLiveData(emptyList())
    val selectedUsers: LiveData<List<User>>
        get() = _selectedUsers

    var updateChatRoom by mutableStateOf<UpdateChatRoom>(Resource.Success(false))
        private set

    var addChatMessage by mutableStateOf<AddChatMessage>(Resource.Success(false))
        private set

    fun getUserInfo() = viewModelScope.launch {
        userUseCase.getUser(currentUser).collect {
            when (it) {
                is Resource.Loading -> showProgress()
                is Resource.Success -> {
                    _user.value = it.data
                    hideProgress()
                }
                is Resource.Error -> ErrorMessage.print(it.message ?: "")
            }
        }
    }

    fun getMessage(roomId: String) = viewModelScope.launch {
        chatUseCase.getAllMessage(roomId).collect {
            when (it) {
                is Resource.Loading -> showProgress()
                is Resource.Success -> {
                    _messages.value = it.data
                    hideProgress()
                }
                is Resource.Error -> ErrorMessage.print(it.message ?: "")
            }
        }
    }

    fun readMessage() {
        val roomId = _chatroom.value?.roomId ?: ""
        firestore.collection(CHAT_COLLECTION_PATH).whereEqualTo(ROOM_ID_FIELD, roomId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null) {
                    val documents = findMydocument(snapshot.documents)
                    updateReadUsers(documents, roomId)
                }
            }
    }

    private fun updateReadUsers(documents: List<DocumentSnapshot>, roomId: String) {
        for (document in documents) {
            val data =
                document.toObject(ChatMessage::class.java)?.readUsers ?: mutableMapOf()
            data.put(currentUser, true)
            document.reference.update(READ_USERS_FIELD, data)
            val unreadCount = _chatroom.value?.unreadCount ?: mutableMapOf()
            unreadCount.put(currentUser, 0)
            updateChatRoom(roomId)
        }
    }

    private fun updateChatRoom(roomId: String) = viewModelScope.launch {
        val unreadCount = _chatroom.value?.unreadCount ?: mutableMapOf()
        unreadCount.put(currentUser, 0)
        updateChatRoom = Resource.Loading()
        updateChatRoom = chatUseCase.updateChatRoom(roomId, unreadCount)
    }

    fun findMydocument(documents: List<DocumentSnapshot>): List<DocumentSnapshot> =
        documents.filter {
            it.toObject(ChatMessage::class.java)?.isNotcontainUid(currentUser) ?: false
        }

    fun writeMessage(editText: String) {
        _buttonSendMessage.value = Event(Unit)
        editTextMessage.value = ""
        val chatMessage = getChatMessage(editText, "1")
        setChatMessage(chatMessage)
        updateLastMessaage(editText)
        updateUnreadCount()
    }

    private fun getChatMessage(editText: String, messageType: String): ChatMessage {
        val chatMessage = ChatMessage(
            _chatroom.value?.roomId ?: "",
            _user.value?.userphoto ?: "",
            currentUser,
            _user.value?.usernm ?: "",
            editText,
            messageType,
        )
        return chatMessage
    }

    private fun updateLastMessaage(editText: String) {
        firestore.collection(CHATROOMS_COLLECTION_PATH)
            .document(_chatroom.value?.roomId ?: "")
            .update(LAST_MESSAGE_FIELD, editText)
        firestore.collection(CHATROOMS_COLLECTION_PATH)
            .document(_chatroom.value?.roomId ?: "")
            .update(LAST_DATE_FIELD, Date().time.toString())
    }

    private fun updateUnreadCount() {
        val unreadCount = _chatroom.value?.unreadCount ?: mutableMapOf()
        for (user in _chatroom.value?.users ?: listOf()) {
            if (user != currentUser) {
                var count = unreadCount.get(user) ?: 0
                unreadCount.put(user, count.plus(1))
            }
        }
        firestore.collection(CHATROOMS_COLLECTION_PATH)
            .document(_chatroom.value?.roomId ?: "")
            .update(UNREAD_COUNT_FIELD, unreadCount)
    }

    fun setChatMessage(chatMessage: ChatMessage) = viewModelScope.launch {
        addChatMessage = Resource.Loading()
        addChatMessage = chatUseCase.setChatMessage(chatMessage)
    }

    fun getChatRoom(chatroom: ChatRoom) {
        _chatroom.value = chatroom
    }

    fun sendMessageImage(uri: String) {
        val message = getChatMessage(uri, "2")
        setChatMessage(message)
        updateLastMessaage("사진을 보냈습니다.")
        updateUnreadCount()
    }

    fun buttonUploadMessageImage() {
        _buttonImageSelection.value = Event(Unit)
    }

    fun addChatUsers() {
        _buttonAddUsers.value = Event(Unit)
        val unreadCount = _chatroom.value?.unreadCount ?: mutableMapOf()
        val users = _chatroom.value?.users ?: mutableListOf()
        val userCount = _chatroom.value?.userCount?.plus(_selectedUsers.value?.size ?: 0)
        val userPhoto = _chatroom.value?.photo ?: mutableMapOf()

        for (user in _selectedUsers.value ?: emptyList()) {
            unreadCount.put(user.uid, 0)
            users.add(user.uid)
            userPhoto.put(user.uid, user.userphoto)
        }
        updateChatRoomUsers(unreadCount, users, userCount, userPhoto)
    }

    private fun updateChatRoomUsers(
        unreadCount: MutableMap<String, Int>,
        addedUsers: MutableList<String>,
        userCount: Int?,
        userPhoto: MutableMap<String, String>,
    ) {
        firestore.collection(CHATROOMS_COLLECTION_PATH)
            .document(_chatroom.value?.roomId ?: "")
            .update(UNREAD_COUNT_FIELD, unreadCount)
        firestore.collection(CHATROOMS_COLLECTION_PATH)
            .document(_chatroom.value?.roomId ?: "")
            .update(USERS_FIELD, addedUsers)
        firestore.collection(CHATROOMS_COLLECTION_PATH)
            .document(_chatroom.value?.roomId ?: "")
            .update(USER_COUNT_FIELD, userCount)
        firestore.collection(CHATROOMS_COLLECTION_PATH)
            .document(_chatroom.value?.roomId ?: "")
            .update(PHOTO_FIELD, userPhoto)
    }

    fun setSelectedUsers(users: MutableList<User>) {
        _selectedUsers.value = users
    }
}
