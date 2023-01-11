package com.qure.domain.usecase.chat

import com.qure.domain.repository.ChatRepository
import javax.inject.Inject

class UpdateChatRoomUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(roomId: String, unreadCount: MutableMap<String, Int>) =
        chatRepository.updateChatRoom(roomId, unreadCount)
}