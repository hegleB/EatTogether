package com.qure.eattogether2.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BarcodeTime(

    var barcodetime : Long=0

) : Parcelable
