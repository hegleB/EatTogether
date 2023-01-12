package com.qure.domain.model

import java.io.Serializable

sealed class PostModel {
    data class Post(
        var uid: String = "",
        var writer: String = "",
        var title: String = "",
        var category: String = "",
        var content: String = "",
        var userimage: String = "",
        var timestamp: String = "",
        var key: String = "",
        var likecount: ArrayList<String> = arrayListOf(),
        var commentsCount: String = "",
        var postImages: ArrayList<String> = arrayListOf()
    ) : Serializable

    data class PostImage(
        var postImage: String = ""
    ) : Serializable
}