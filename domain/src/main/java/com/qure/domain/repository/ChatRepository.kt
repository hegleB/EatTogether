package com.qure.domain.repository

import com.qure.domain.model.ChatRoom
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getAllChatRoom(uid: String): Flow<Resource<List<ChatRoom>, String>>
    suspend fun setChatRoom(chatroom: ChatRoom): Flow<Resource<String, String>>
}