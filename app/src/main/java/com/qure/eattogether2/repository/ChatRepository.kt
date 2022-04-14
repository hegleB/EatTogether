package com.qure.eattogether2.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.qure.eattogether2.data.ChatComment
import com.qure.eattogether2.data.ChatRoom

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    fun getAllChatRoom(currentUid : String) : Flow<List<ChatRoom>>{

        return callbackFlow{
            val callback = firestore.collection("chatrooms")
                .whereEqualTo("room", true).orderBy("lastDate", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {

                        close(e)

                    } else {

                        val room = snapshots!!.toObjects(ChatRoom::class.java)
                        val chatroomList = arrayListOf<ChatRoom>()
                        for (i in room) {
                            if (i.users.contains(currentUid)) {
                                chatroomList.add(i)
                            }
                        }

                        offer(chatroomList)
                    }
                }
            awaitClose {
                callback.remove()
            }
        }
    }


    fun getChatMessage(roomId : String) : Flow<MutableList<ChatComment>> {

        return callbackFlow {
            val callback = firestore.collectionGroup("chat").whereEqualTo("roomId", roomId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        close(e)
                    } else {

                        offer(snapshot!!.toObjects(ChatComment::class.java))
                    }
                }
            awaitClose {
                callback.remove()
            }
        }
    }



        companion object {
            const val TAG = "People Repository"
        }

    }