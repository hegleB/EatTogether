package com.qure.domain.model

import java.io.Serializable


data class User(
    var userid: String="",
    var uid: String="",
    var usernm: String="",
    var token: String="",
    var userphoto: String="",
    var usermsg: String=""
) : Serializable {
    fun isSameUid(currentUid: String): Boolean =
        this.uid == currentUid
}