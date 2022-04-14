package com.qure.eattogether2.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Setting(

    var message : Boolean = true,
    var vibration: Boolean=true,
    var sound : Boolean = true,
    var notification_time : Long = 0

): Parcelable
