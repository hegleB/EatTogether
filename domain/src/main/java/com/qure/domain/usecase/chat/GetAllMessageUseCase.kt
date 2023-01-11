package com.qure.domain.usecase.chat

import com.qure.domain.repository.ChatRepository
import javax.inject.Inject

class GetAllMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(chatRoomId: String) = chatRepository.getAllMessage(chatRoomId)
}