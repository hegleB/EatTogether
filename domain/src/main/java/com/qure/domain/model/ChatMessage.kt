package com.qure.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatMessage(
    var roomId: String = "",
    var userImage: String = "",
    var uid: String = "",
    var usernm: String = "",
    var message: String = "",
    var messagetype: String = "",
    val timestamp: String = System.currentTimeMillis().toString(),
    val readUsers: MutableMap<String, Boolean> = mutableMapOf(uid to true)
) : Parcelable {

    fun isNotcontainUid(currentUser: String) =
        !this.readUsers.containsKey(currentUser)

    fun isSameChatRoomId(chatRoomId: String) =
        this.roomId == chatRoomId
}