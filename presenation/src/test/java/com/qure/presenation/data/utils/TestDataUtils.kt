package com.qure.presenation.data.utils

import com.qure.domain.model.*

class TestDataUtils {

    companion object {
        val posts = listOf(
            PostModel.Post(
                uid = "abs",
                key = "abs",
                writer = "abs",
                title = "abs",
                content = "abs",
                category = "한식",
                likecount = arrayListOf("a", "b"),
                timestamp = "1",
                postImages = arrayListOf("0", "1"),
                commentsCount = "1"
            ),
            PostModel.Post(
                uid = "a",
                key = "a",
                writer = "a",
                title = "a",
                content = "a",
                category = "중식",
                likecount = arrayListOf("abs"),
                timestamp = "2",
                userimage = "a",
            ),
            PostModel.Post(
                uid = "b",
                key = "b",
                writer = "b",
                title = "b",
                content = "b",
                category = "중식",
                likecount = arrayListOf("abs"),
                timestamp = "3",
                userimage = "b",
            ),
            PostModel.Post(
                uid = "abs",
                key = "absa",
                writer = "abs",
                title = "abca",
                content = "absa",
                category = "양식",
                likecount = arrayListOf(),
                timestamp = "4",
                userimage = "abs",
            )
        )

        val comments = listOf(
            Comments(
                comments_uid = "abs",
                comments_usernm = "abs",
                comments_timestamp = "1",
                comments_postkey = "abs",
                comments_commentskey = "absa",
            ),
            Comments(
                comments_uid = "abs",
                comments_usernm = "abs",
                comments_timestamp = "2",
                comments_postkey = "absa",
                comments_commentskey = "aa",
            ),
            Comments(
                comments_uid = "abs",
                comments_usernm = "abs",
                comments_timestamp = "3",
                comments_postkey = "b",
                comments_commentskey = "absb"
            ),
        )

        val recomments = listOf(
            Comments(
                comments_uid = "a",
                comments_usernm = "a",
                comments_timestamp = "3",
                comments_postkey = "abs",
                comments_commentskey = "absa",
                comments_replyKey = "absaa",
                comments_replyTimeStamp = "1",
                comments_depth = 1
            ),

            Comments(
                comments_uid = "abs",
                comments_usernm = "abs",
                comments_timestamp = "3",
                comments_postkey = "abs",
                comments_commentskey = "absa",
                comments_replyKey = "absab",
                comments_replyTimeStamp = "2",
                comments_depth = 1
            ),
        )

        val meeting = mapOf(
            "abs" to 1,
            "a" to 2
        )

        val barcode = mapOf(
            "abs" to "barcode_a"
        )

        val chatRoom = listOf(
            ChatRoom(
                roomId = "abs",
                lastmsg = "abs",
                userCount = 2,
                users = arrayListOf("abs", "a"),
                unreadCount = mutableMapOf("abs" to 1, "a" to 1)
            ),
            ChatRoom(
                roomId = "a",
                lastmsg = "a",
                userCount = 3,
                users = arrayListOf("abs", "a", "b"),
                unreadCount = mutableMapOf("abs" to 0, "a" to 0, "b" to 0)
            )
        )

        val message = listOf(
            ChatMessage(
                roomId = "a",
                message = "a",
                timestamp = "0"
            ),
            ChatMessage(
                roomId = "a",
                message = "b",
                timestamp = "0"
            ),
            ChatMessage(
                roomId = "a",
                message = "ab",
                timestamp = "0"
            )
        )
    }
}