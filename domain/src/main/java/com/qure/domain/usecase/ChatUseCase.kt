package com.qure.domain.usecase

import com.qure.domain.model.ChatMessage
import com.qure.domain.model.ChatRoom
import com.qure.domain.repository.ChatRepository
import javax.inject.Inject

class ChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend fun getAllChatRoom(uid: String) = chatRepository.getAllChatRoom(uid)
    suspend fun getAllMessage(chatRoomId: String) = chatRepository.getAllMessage(chatRoomId)

    suspend fun setChatMessage(chatMessage: ChatMessage) =
        chatRepository.setChatMessage(chatMessage)

    suspend fun setChatRoom(chatroom: ChatRoom) = chatRepository.setChatRoom(chatroom)

    suspend fun updateChatRoom(roomId: String, unreadCount: MutableMap<String, Int>) =
        chatRepository.updateChatRoom(roomId, unreadCount)
}