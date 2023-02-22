package com.qure.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UploadImage(
    val postkey: String = "",
    val imagePath: String = ""
) : Parcelable
