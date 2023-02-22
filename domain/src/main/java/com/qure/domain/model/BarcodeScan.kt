package com.qure.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BarcodeScan(
    var meeting: Int = 0
) : Parcelable
