package com.qure.eattogether2.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.qure.eattogether2.data.ChatComment
import com.qure.eattogether2.data.ChatRoom
import com.qure.eattogether2.data.User
import com.qure.eattogether2.paging.ChatCommentPagingSource
import com.qure.eattogether2.paging.ChatPagingSource
import com.qure.eattogether2.paging.PeoplePagingSource
import com.qure.eattogether2.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatViewModel @ViewModelInject constructor(
    val chatRepository: ChatRepository
) : ViewModel() {

    var messageCount = MutableLiveData<Int>()

    var roomId = MutableLiveData<String>()

    fun getChatRooms(allChatRooms: MutableLiveData<List<ChatRoom>>) = Pager(
        PagingConfig(
            pageSize = 30
        )
    ) {

        ChatPagingSource(allChatRooms)
    }.flow.cachedIn(viewModelScope)



}