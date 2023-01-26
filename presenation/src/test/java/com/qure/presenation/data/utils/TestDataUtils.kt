package com.qure.presenation.data.utils

import com.qure.domain.model.Comments
import com.qure.domain.model.PostModel

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
    }
}