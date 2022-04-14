package com.qure.eattogether2.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatRoom(

    var room : Boolean = false,
    val roomId : String="",
    var title :  String="",
    var photo : MutableMap<String, String> = mutableMapOf(),
    var lastmsg : String="",
    var lastDate : String="",
    var userCount : Int=0,
    var unreadCount : MutableMap<String, Int> = mutableMapOf(),
    var users : ArrayList<String> = arrayListOf()

) : Parcelable
