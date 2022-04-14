package com.qure.eattogether2.data

import android.os.Parcelable

import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var userid: String="",
    var uid: String="",
    var usernm: String="",
    var token: String="",
    var userphoto: String="",
    var usermsg: String=""
) : Parcelable