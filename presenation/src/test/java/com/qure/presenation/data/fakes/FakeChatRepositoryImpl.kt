package com.qure.presenation.data.fakes

import com.qure.domain.model.ChatMessage
import com.qure.domain.model.ChatRoom
import com.qure.domain.model.PostModel
import com.qure.domain.repository.*
import com.qure.domain.utils.Resource
import com.qure.presenation.data.utils.TestDataUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf

class FakeChatRepositoryImpl : ChatRepository {

    private val chatRoom = TestDataUtils.chatRoom
    private val message = TestDataUtils.message
    override suspend fun getAllChatRoom(uid: String): Flow<ChatRoomResource> {
        return flowOf(Resource.Success(chatRoom))
    }

    override suspend fun setChatRoom(chatroom: ChatRoom): AddChatRoom {
        val addedChatRoom = chatRoom.toMutableList()
        addedChatRoom.add(chatroom)
        return if (chatRoom.size + 1 == addedChatRoom.size) {
            Resource.Success(true)
        } else {
            Resource.Success(false)
        }
    }

    override suspend fun getAllMessage(chatRoomId: String): Flow<MessageResource> {
        val chatMessage = message.filter { it.isSameChatRoomId(chatRoomId) }
        return flowOf(Resource.Success(chatMessage))
    }

    override suspend fun setChatMessage(chatMessage: ChatMessage): AddChatMessage {
        val addedMessage = message.toMutableList()
        addedMessage.add(chatMessage)
        return if (message.size + 1 == addedMessage.size) {
            Resource.Success(true)
        } else {
            Resource.Success(false)
        }
    }

    override suspend fun updateChatRoom(
        roomId: String,
        unreadCount: Map<String, Int>
    ): UpdateChatRoom {
        val chatroom = chatRoom.find { it.isSameRoomId(roomId) } ?: ChatRoom()

        return if (unreadCount.values.containsAll(chatroom.unreadCount.map { it.value.minus(1) })) {
            Resource.Success(true)
        } else {
            Resource.Success(false)
        }
    }
}