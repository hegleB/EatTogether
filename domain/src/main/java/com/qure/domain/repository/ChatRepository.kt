package com.qure.domain.repository

import com.qure.domain.model.ChatMessage
import com.qure.domain.model.ChatRoom
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getAllChatRoom(uid: String): Flow<Resource<List<ChatRoom>, String>>
    suspend fun setChatRoom(chatroom: ChatRoom): Flow<Resource<String, String>>
    suspend fun getAllMessage(chatRoomId: String): Flow<Resource<List<ChatMessage>, String>>
    suspend fun setChatMessage(chatMessage: ChatMessage): Flow<Resource<String, String>>
    suspend fun updateChatRoom(
        roomId: String,
        unreadCount: Map<String, Int>
    ): Flow<Resource<String, String>>

    suspend fun updateChat(
        roomId: String,
        readUsers: MutableMap<String, Boolean>
    ): Flow<Resource<String, String>>
}