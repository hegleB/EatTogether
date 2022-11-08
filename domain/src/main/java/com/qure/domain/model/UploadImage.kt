package com.qure.domain.model

import java.io.Serializable

data class UploadImage(
    val postkey : String="",
    val imagePath: String=""
): Serializable
