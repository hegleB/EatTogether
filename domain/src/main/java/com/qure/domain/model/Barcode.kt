package com.qure.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Barcode(
    var barcode: String = ""
) : Parcelable
