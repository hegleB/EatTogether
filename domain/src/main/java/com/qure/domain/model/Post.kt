package com.qure.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


sealed class PostModel {
    @Parcelize
    data class Post(
        val uid: String = "",
        val writer: String = "",
        val title: String = "",
        val category: String = "",
        val content: String = "",
        val userimage: String = "",
        val timestamp: String = System.currentTimeMillis().toString(),
        val key: String = "",
        var likecount: ArrayList<String> = arrayListOf(),
        val commentsCount: String = "0",
        val postImages: ArrayList<String> = arrayListOf()
    ) : Parcelable {
        fun isSameKey(otherKey: String): Boolean =
            this.key == otherKey

        fun isSameCategory(categoryName: String) =
            this.category == categoryName

        fun isSameUid(uid: String) =
            this.uid == uid

        fun updateLike(likeList: ArrayList<String>): Post {
            this.likecount = likeList
            return this
        }

        fun isSameLikeCount(count: Int): Boolean =
            this.likecount.size == count

        fun isClickedPostLike(uid: String): Boolean =
            this.likecount.contains(uid)

    }

    @Parcelize
    data class PostImage(
        val postkey: String = "",
        val postImage: String = ""
    ) : Parcelable
}