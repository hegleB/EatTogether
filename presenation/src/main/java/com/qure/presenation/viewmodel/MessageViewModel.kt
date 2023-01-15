package com.qure.presenation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Constraints
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.qure.domain.utils.ErrorMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.domain.model.*
import com.qure.domain.repository.AddChatMessage
import com.qure.domain.repository.UpdateChatRoom
import com.qure.domain.usecase.chat.*
import com.qure.domain.usecase.people.GetUserInfoUseCase
import com.qure.domain.utils.Constants
import com.qure.domain.utils.Resource
import com.qure.presenation.Event
import com.qure.presenation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    val firestore: FirebaseFirestore,
    val firebaseAuth: FirebaseAuth,
    val getAllMessageUsecase: GetAllMessageUseCase,
    val setChatMessageUsecase: SetChatMessageUsecase,
    val getUserInfoUseCase: GetUserInfoUseCase,
    val updateChatRoomUseCase: UpdateChatRoomUseCase
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
        getUserInfoUseCase(currentUser).collect {
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
        getAllMessageUsecase(roomId).collect {
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
        firestore.collection("chat").whereEqualTo("roomId", roomId)
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
            document.reference.update("readUsers", data)
            val unreadCount = _chatroom.value?.unreadCount ?: mutableMapOf()
            unreadCount.put(currentUser, 0)
            updateChatRoom(roomId)
        }
    }

    private fun updateChatRoom(roomId: String) = viewModelScope.launch {
        val unreadCount = _chatroom.value?.unreadCount ?: mutableMapOf()
        unreadCount.put(currentUser, 0)
        updateChatRoom = Resource.Loading()
        updateChatRoom = updateChatRoomUseCase(roomId, unreadCount)
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
            messageType
        )
        return chatMessage
    }

    private fun updateLastMessaage(editText: String) {
        firestore.collection(Constants.CHATROOMS_COLLECTION_PATH)
            .document(_chatroom.value?.roomId ?: "")
            .update("lastmsg", editText)
        firestore.collection(Constants.CHATROOMS_COLLECTION_PATH)
            .document(_chatroom.value?.roomId ?: "")
            .update("lastDate", Date().time.toString())
    }

    private fun updateUnreadCount() {
        val unreadCount = _chatroom.value?.unreadCount ?: mutableMapOf()
        for (user in _chatroom.value?.users ?: listOf()) {
            if (user != currentUser) {
                var count = unreadCount.get(user) ?: 0
                unreadCount.put(user, count.plus(1))
            }
        }
        firestore.collection(Constants.CHATROOMS_COLLECTION_PATH)
            .document(_chatroom.value?.roomId ?: "")
            .update("unreadCount", unreadCount)
    }

    fun setChatMessage(chatMessage: ChatMessage) = viewModelScope.launch {
        addChatMessage = Resource.Loading()
        addChatMessage = setChatMessageUsecase(chatMessage)
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

        for (user in _selectedUsers.value ?: emptyList()) {
            unreadCount.put(user.uid, 0)
            users.add(user.uid)
        }
        updateChatRoomUsers(unreadCount, users, userCount)
    }

    private fun updateChatRoomUsers(
        unreadCount: MutableMap<String, Int>,
        addedUsers: MutableList<String>,
        userCount: Int?
    ) {
        firestore.collection(Constants.CHATROOMS_COLLECTION_PATH)
            .document(_chatroom.value?.roomId ?: "")
            .update("unreadCount", unreadCount)
        firestore.collection(Constants.CHATROOMS_COLLECTION_PATH)
            .document(_chatroom.value?.roomId ?: "")
            .update("users", addedUsers)
        firestore.collection(Constants.CHATROOMS_COLLECTION_PATH)
            .document(_chatroom.value?.roomId ?: "")
            .update("userCount", userCount)
    }

    fun setSelectedUsers(users: MutableList<User>) {
        _selectedUsers.value = users
    }
}