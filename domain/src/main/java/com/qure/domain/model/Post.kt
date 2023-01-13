package com.qure.domain.model

import java.io.Serializable

sealed class PostModel {
    data class Post(
        val uid: String = "",
        val writer: String = "",
        val title: String = "",
        val category: String = "",
        val content: String = "",
        val userimage: String = "",
        val timestamp: String = "",
        val key: String = "",
        val likecount: ArrayList<String> = arrayListOf(),
        val commentsCount: String = "",
        val postImages: ArrayList<String> = arrayListOf()
    ) : Serializable

    data class PostImage(
        val postkey: String = "",
        val postImage: String = ""
    ) : Serializable
}