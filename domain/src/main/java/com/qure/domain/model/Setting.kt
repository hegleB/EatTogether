package com.qure.domain.model

import java.io.Serializable

data class Setting(

    var message : Boolean = true,
    var vibration: Boolean=true,
    var sound : Boolean = true,
    var notification_time : Long = 0

): Serializable