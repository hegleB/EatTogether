package com.qure.domain.model

import java.io.Serializable

data class ChatMessage(
    var roomId : String="",
    var userImage : String="",
    var uid : String="",
    var usernm : String="",
    var message : String="",
    var messagetype : String="",
    var timestamp : String="",
    var readUsers : MutableMap<String, Boolean> = mutableMapOf()
) : Serializable {

    fun isNotcontainUid(currentUser: String) =
        !this.readUsers.containsKey(currentUser)
}