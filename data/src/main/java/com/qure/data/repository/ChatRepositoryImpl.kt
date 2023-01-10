package com.qure.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.qure.domain.model.ChatRoom
import com.qure.domain.model.PostModel
import com.qure.domain.repository.ChatRepository
import com.qure.domain.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

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
}