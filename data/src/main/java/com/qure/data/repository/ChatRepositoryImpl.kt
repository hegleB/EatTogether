package com.qure.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.qure.domain.model.ChatMessage
import com.qure.domain.model.ChatRoom
import com.qure.domain.repository.ChatRepository
import com.qure.domain.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

class ChatRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ChatRepository {
    override suspend fun getAllChatRoom(uid: String): Flow<Resource<List<ChatRoom>, String>> {
        return callbackFlow {
            val callback = firestore.collection("chatrooms")
                .addSnapshotListener { snapshot, e ->
                    if (e == null) {
                        val isEmpty = snapshot?.isEmpty ?: false
                        val chatRoom = mutableListOf<ChatRoom>()

                        if (!isEmpty) {
                            val room = snapshot?.toObjects(ChatRoom::class.java)!!
                            room.forEach { chatroom ->
                                if (chatroom.users.contains(uid)) {
                                    chatRoom.add(chatroom)
                                }
                            }
                            this.trySendBlocking(Resource.Success(chatRoom))
                        } else {
                            this.trySendBlocking(Resource.Success(listOf()))
                        }
                    } else {
                        this.trySendBlocking(Resource.Error(e.message))
                    }
                }
            awaitClose {
                callback.remove()
            }
        }
    }

    override suspend fun setChatRoom(chatroom: ChatRoom): Flow<Resource<String, String>> {
        return callbackFlow {
            val callback = firestore.collection("chatrooms")
                .document(chatroom.roomId)
                .set(chatroom)
                .addOnSuccessListener {
                    this.trySendBlocking(Resource.Success("채팅방 생성 성공"))
                }
                .addOnFailureListener {
                    this.trySendBlocking(Resource.Error("채팅방 생성 실패"))
                }
            awaitClose {
                callback.isCanceled
            }
        }
    }

    override suspend fun getAllMessage(chatRoomId: String): Flow<Resource<List<ChatMessage>, String>> {
        return callbackFlow {
            val callback = firestore.collection("chat")
                .whereEqualTo("roomId", chatRoomId)
                .addSnapshotListener { snapshot, e ->
                    if (e == null) {
                        val isEmpty = snapshot?.isEmpty ?: false
                        if (!isEmpty) {
                            val message = snapshot?.toObjects(ChatMessage::class.java)!!
                            this.trySendBlocking(Resource.Success(message))
                        } else {
                            this.trySendBlocking(Resource.Empty("데이터가 없습니다."))
                        }
                    } else {
                        this.trySendBlocking(Resource.Error(e.message))
                    }

                }
            awaitClose {
                callback.remove()
            }
        }
    }

    override suspend fun setChatMessage(chatMessage: ChatMessage): Flow<Resource<String, String>> {
        return callbackFlow {
            val currentTime = Date().time
            val callback = firestore.collection("chat")
                .document(currentTime.toString())
                .set(chatMessage)
                .addOnSuccessListener {
                    this.trySendBlocking(Resource.Success("메세지 저장 성공"))
                }
                .addOnFailureListener {
                    this.trySendBlocking(Resource.Error("메세지 저장 실패"))
                }
            awaitClose {
                callback.isCanceled
            }
        }
    }

    override suspend fun updateChatRoom(
        roomId: String,
        unreadCount: Map<String, Int>
    ): Flow<Resource<String, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback = firestore.collection("chatrooms")
                .document(roomId)
                .update("unreadCount", unreadCount)
                .addOnSuccessListener { this.trySendBlocking(Resource.Success("성공")) }
                .addOnFailureListener { this.trySendBlocking(Resource.Error(it.message)) }
            awaitClose {
                callback.isCanceled
            }
        }
    }

    override suspend fun updateChat(
        roomId: String,
        readUsers: MutableMap<String, Boolean>
    ): Flow<Resource<String, String>> {
        return callbackFlow {
            this.trySendBlocking(Resource.Loading())
            val callback = firestore.collection("chat").whereEqualTo("roomId", roomId)
                .addSnapshotListener { snapshot, e ->
                    if (e == null) {
                        val isEmpty = snapshot?.isEmpty ?: false
                        if (!isEmpty) {
                            for (chat in snapshot?.documents!!) {
                                chat.reference.update("readUsers", readUsers)
                            }
                            this.trySendBlocking(Resource.Success("읽은 메세지 업데이트 성공"))
                        } else {
                            this.trySendBlocking(Resource.Empty("메세지가 존재하지 않습니다."))
                        }
                    } else {
                        this.trySendBlocking(Resource.Error(e.message))
                    }
                }
            awaitClose {
                callback.remove()
            }
        }
    }
}