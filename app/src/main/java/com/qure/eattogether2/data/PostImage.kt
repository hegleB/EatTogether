package com.qure.eattogether2.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostImage(

    val postkey : String="",
    val imagePath: String=""

): Parcelable
