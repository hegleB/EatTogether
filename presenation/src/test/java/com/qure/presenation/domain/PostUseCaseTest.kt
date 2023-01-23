package com.qure.presenation.domain

import com.qure.domain.model.PostModel
import com.qure.presenation.BaseTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PostUseCaseTest : BaseTest() {
    override fun init() {

    }

    @Test
    fun `모든 게시물을 가져온다`() = runTest {
        val expected = listOf(
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
        assertThat(postUseCase.getAllPost().first().data).isEqualTo(expected)

    }

    @Test
    fun `해당하는 키의 게시물을 확인한다`() = runTest {
        assertThat(postUseCase.checkPost("abs").first().data).isEqualTo(
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
            ),
        )
        assertThat(postUseCase.checkPost("").first().data).isEqualTo(PostModel.Post())
    }

    @Test
    fun `카테고리에 해당하는 게시물을 가져온다`() = runTest {
        assertThat(postUseCase.getCategoryPost("중식").first().data).isEqualTo(
            listOf(
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
            )
        )
    }

    @Test
    fun `해당 게시물을 좋아요 개수를 업데이트한다`() = runTest {
        assertThat(postUseCase.updateLike(arrayListOf("a", "b", "c"), "abs").data).isEqualTo(true)
        assertThat(postUseCase.updateLike(arrayListOf("a"), "abs").data).isEqualTo(true)
    }

    @Test
    fun `생성된 게시물을 생성된 시간을 기준으로 내림차순으로 가져온다`() = runTest {
        assertThat(postUseCase.getProfileCreatedPosts("abs").first().data).isEqualTo(
            listOf(
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
                ),
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
                ),
            )
        )
    }

    @Test
    fun `생성된 게시물에 좋아요를 누른 게시물을 시간 기준 내림차순으로 가져온다`() = runTest {
        assertThat(postUseCase.getProfileLikedPosts("abs").first().data).isEqualTo(
            listOf(
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
            )
        )
    }

    @Test
    fun `유저가 작성한 댓글에 해당하는 게시물을 가져온다`() = runTest {
        assertThat(postUseCase.getProfileCommentsCreatedPosts("abs").first().data).isEqualTo(
            listOf(
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
                    key = "abs",
                    writer = "abs",
                    title = "abs",
                    content = "abs",
                    category = "한식",
                    likecount = arrayListOf("a", "b"),
                    timestamp = "1",
                    postImages = arrayListOf("0", "1"),
                ),
            )
        )
    }

    @Test
    fun `키에 해당하는 게시물 이미지를 가져온다`() = runTest {
        assertThat(postUseCase.getPostImage("abs").first().data).isEqualTo(
            listOf(
                PostModel.PostImage("abs", "0"),
                PostModel.PostImage("abs", "1")
            )
        )
    }
}