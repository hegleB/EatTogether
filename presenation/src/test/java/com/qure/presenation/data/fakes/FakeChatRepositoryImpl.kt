package com.qure.presenation.data.fakes

import com.qure.domain.model.ChatMessage
import com.qure.domain.model.ChatRoom
import com.qure.domain.repository.*
import com.qure.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FakeChatRepositoryImpl : ChatRepository {
    override suspend fun getAllChatRoom(uid: String): Flow<ChatRoomResource> {
        return callbackFlow {
            if (uid.isNullOrBlank()) {
                Resource.Success(false)
            } else {
                Resource.Success(true)
            }
        }
    }

    override suspend fun setChatRoom(chatroom: ChatRoom): AddChatRoom {
        return if (chatroom == null) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }

    override suspend fun getAllMessage(chatRoomId: String): Flow<MessageResource> {
        return callbackFlow {
            if (chatRoomId.isNullOrBlank()) {
                Resource.Success(false)
            } else {
            Resource.Success(true)
        }
        }
    }

    override suspend fun setChatMessage(chatMessage: ChatMessage): AddChatMessage {
        return if (chatMessage == null) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }

    override suspend fun updateChatRoom(
        roomId: String,
        unreadCount: Map<String, Int>
    ): UpdateChatRoom {
        return if (roomId.isNullOrBlank()) {
            Resource.Success(false)
        } else {
            Resource.Success(true)
        }
    }
}