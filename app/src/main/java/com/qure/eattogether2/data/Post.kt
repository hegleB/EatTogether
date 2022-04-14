package com.qure.eattogether2.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Post(

    var uid : String="",
    var writer : String="",
    var title : String="",
    var category : String="",
    var content : String="",
    var userimage : String="",
    var timestamp : String="",
    var key : String="",
    var likecount : ArrayList<String> = arrayListOf(),
    var commentsCount : String="",
    var postImages : ArrayList<String> = arrayListOf()
) : Parcelable
