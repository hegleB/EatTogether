package com.qure.domain.usecase.chat

import com.qure.domain.repository.ChatRepository
import javax.inject.Inject

class GetAllChatRoomUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(uid: String) = chatRepository.getAllChatRoom(uid)
}