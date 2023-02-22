package com.qure.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var userid: String = "",
    var uid: String = "",
    var usernm: String = "",
    var token: String = "",
    var userphoto: String = "",
    var usermsg: String = ""
) : Parcelable {
    fun isSameUid(currentUid: String): Boolean =
        this.uid == currentUid
}