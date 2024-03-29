package com.qure.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.qure.domain.model.ChatMessage
import com.qure.domain.model.ChatRoom
import com.qure.domain.repository.AddChatMessage
import com.qure.domain.repository.AddChatRoom
import com.qure.domain.repository.ChatRepository
import com.qure.domain.repository.UpdateChatRoom
import com.qure.domain.utils.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*

class ChatRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    override suspend fun getAllChatRoom(uid: String) = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collection(CHATROOMS_COLLECTION_PATH)
            .whereArrayContains(USERS_FIELD, uid)
            .addSnapshotListener { snapshot, e ->
                val chatRoomResource = if (snapshot != null) {
                    val chatRoomObjects = snapshot?.toObjects(ChatRoom::class.java)
                    val chatRooms = getChatRooms(chatRoomObjects, uid)
                    Resource.Success(chatRooms)
                } else {
                    Resource.Error(e?.message)
                }
                trySend(chatRoomResource)
            }
        awaitClose {
            callback.remove()
        }
    }

    private fun getChatRooms(chatRooms: List<ChatRoom>, uid: String): List<ChatRoom> {
        val result = mutableListOf<ChatRoom>()
        for (chatRoom in chatRooms) {
            if (chatRoom.users.contains(uid)) {
                result.add(chatRoom)
            }
        }
        return result
    }


    override suspend fun setChatRoom(chatroom: ChatRoom): AddChatRoom {
        return try {
            firestore.collection(CHATROOMS_COLLECTION_PATH)
                .document(chatroom.roomId)
                .set(chatroom)
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun getAllMessage(chatRoomId: String) = callbackFlow {
        trySend(Resource.Loading())
        val callback = firestore.collection(CHAT_COLLECTION_PATH)
            .whereEqualTo(ROOM_ID_FIELD, chatRoomId)
            .addSnapshotListener { snapshot, e ->
                val messageResource = if (snapshot != null) {
                    val message = snapshot.toObjects(ChatMessage::class.java)
                    Resource.Success(message)
                } else {
                    Resource.Error(e?.message)
                }
                trySend(messageResource)
            }
        awaitClose {
            callback.remove()
        }
    }


    override suspend fun setChatMessage(chatMessage: ChatMessage): AddChatMessage {
        return try {
            val currentTime = Date().time
            firestore.collection(CHAT_COLLECTION_PATH)
                .document(currentTime.toString())
                .set(chatMessage)
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun updateChatRoom(
        roomId: String,
        unreadCount: Map<String, Int>
    ): UpdateChatRoom {
        return try {
            firestore.collection(CHATROOMS_COLLECTION_PATH)
                .document(roomId)
                .update(UNREAD_COUNT_FIELD, unreadCount)
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}