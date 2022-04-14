package com.qure.eattogether2.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatComment(
    var roomId : String="",
    var userImage : String="",
    var uid : String="",
    var usernm : String="",
    var message : String="",
    var messagetype : String="",
    var timestamp : String="",
    var readUsers : MutableMap<String, Boolean> = mutableMapOf()
) : Parcelable