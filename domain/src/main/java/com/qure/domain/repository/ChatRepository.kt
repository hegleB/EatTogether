package com.qure.domain.repository

import com.qure.domain.model.ChatMessage
import com.qure.domain.model.ChatRoom
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

typealias ChatRoomResource = Resource<List<ChatRoom>, String>
typealias MessageResource = Resource<List<ChatMessage>, String>
typealias AddChatRoom = Resource<Boolean, String>
typealias AddChatMessage = Resource<Boolean, String>
typealias UpdateChatRoom = Resource<Boolean, String>

interface ChatRepository {
    suspend fun getAllChatRoom(uid: String): Flow<ChatRoomResource>
    suspend fun setChatRoom(chatroom: ChatRoom): AddChatRoom
    suspend fun getAllMessage(chatRoomId: String): Flow<MessageResource>
    suspend fun setChatMessage(chatMessage: ChatMessage): AddChatMessage
    suspend fun updateChatRoom(
        roomId: String,
        unreadCount: Map<String, Int>
    ): UpdateChatRoom
}