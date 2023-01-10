package com.qure.domain.usecase.chat

import com.qure.domain.model.ChatRoom
import com.qure.domain.repository.ChatRepository
import javax.inject.Inject

class SetChatRoomUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(chatroom: ChatRoom) = chatRepository.setChatRoom(chatroom)
}