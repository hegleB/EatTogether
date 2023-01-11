package com.qure.domain.usecase.chat

import com.qure.domain.model.ChatMessage
import com.qure.domain.repository.ChatRepository
import javax.inject.Inject

class SetChatMessageUsecase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(chatMessage: ChatMessage) =
        chatRepository.setChatMessage(chatMessage)
}