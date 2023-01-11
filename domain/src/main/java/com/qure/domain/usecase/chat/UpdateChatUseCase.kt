package com.qure.domain.usecase.chat

import com.qure.domain.repository.ChatRepository
import javax.inject.Inject

class UpdateChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(roomId: String, readUsers: MutableMap<String, Boolean>) =
        chatRepository.updateChat(roomId, readUsers)
}