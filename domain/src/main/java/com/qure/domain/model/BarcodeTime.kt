package com.qure.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BarcodeTime(
    var barcodetime: Long = 0
) : Parcelable
