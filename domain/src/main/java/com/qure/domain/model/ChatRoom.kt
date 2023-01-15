package com.qure.domain.model

import java.io.Serializable


data class ChatRoom(
    var room: Boolean = false,
    val roomId: String = "",
    var title: String = "",
    var photo: MutableMap<String, String> = mutableMapOf(),
    var lastmsg: String = "",
    var lastDate: String = "",
    var userCount: Int = 0,
    var unreadCount: MutableMap<String, Int> = mutableMapOf(),
    var users: ArrayList<String> = arrayListOf()

) : Serializable {

    fun isContainUid(uid: String): Boolean =
        this.users.contains(uid)

    fun isCorrectOneToOneChatroom(): Boolean =
        this.users.size <= 2

    fun removeChatRommUsers(chatUsers: List<User>): List<User> {
        var chatusers = chatUsers.toMutableList()
        for (user in this.users) {
            chatusers = getChatRoomUsers(chatusers, user)
        }
        return chatUsers
    }

    private fun getChatRoomUsers(chatusers: MutableList<User>, user: String): MutableList<User> =
        chatusers.filter { !it.isSameUid(user) }.toMutableList()

}
