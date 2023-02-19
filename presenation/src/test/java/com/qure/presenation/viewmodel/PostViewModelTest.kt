package com.qure.presenation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.qure.domain.model.PostModel
import com.qure.domain.model.User
import com.qure.presenation.BaseTest
import com.qure.presenation.CoroutinesTestExtension
import com.qure.presenation.InstantTaskExecutorExtension
import com.qure.presenation.data.utils.TestDataUtils
import com.qure.presenation.getOrAwaitValue
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class, CoroutinesTestExtension::class)
class PostViewModelTest : BaseTest() {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: PostViewModel
    override fun init() {
        viewModel = PostViewModel(commentUseCase, postUseCase, userUseCase, uploadUseCase)
        with(viewModel) {
            getCategoryName("양식")
            getProfileUid("abs")
            getCurrentUid("abs")
            getPostKey("abs")
        }
    }

    @Test
    fun `모든 게시물을 가져온다`() = runTest {
        with(viewModel) {
            getAllPost()
            assertThat(postList.getOrAwaitValue()).isEqualTo(
                TestDataUtils.posts
            )
        }
    }

    @Test
    fun `카테고리별 게시물을 가져온다`() = runTest {
        with(viewModel) {
            getCategoryPost()
            assertThat(categoryPostList.getOrAwaitValue()).isEqualTo(
                listOf(TestDataUtils.posts[3])
            )
        }
    }

    @Test
    fun `해당하는 유저 프로필에 생성된 게시물을 가져온다`() = runTest {
        with(viewModel) {
            getProfileCreatedPosts()
            assertThat(viewModel.profileCreatedPost.getOrAwaitValue()).isEqualTo(
                listOf(TestDataUtils.posts[3], TestDataUtils.posts[0])
            )
        }
    }

    @Test
    fun `해당하는 유저 프로필에 좋아요 누른 게시물을 가져온다`() = runTest {
        with(viewModel) {
            getProfileLikedPosts()
            assertThat(viewModel.profileLikedPost.getOrAwaitValue()).isEqualTo(
                listOf(TestDataUtils.posts[2], TestDataUtils.posts[1])
            )
        }
    }

    @Test
    fun `해당하는 유저 프로필에 댓글을 작성한 게시물을 가져온다`() = runTest {
        with(viewModel) {
            getProfileCommentsCreatedPosts()
            assertThat(profileCommentsPost.getOrAwaitValue()).isEqualTo(
                listOf(TestDataUtils.posts[3], TestDataUtils.posts[2], TestDataUtils.posts[0])
            )
        }
    }

    @Test
    fun `현재 유저 정보를 가져온다`() = runTest {
        with(viewModel) {
            getUserInfo()
            assertThat(writer.getOrAwaitValue()).isEqualTo(
                User("abs@abs.com", "abs", "abs")
            )
        }
    }

    @Test
    fun `게시물에 작성된 모든 댓글을 가져온다`() = runTest {
        with(viewModel) {
            getComments()
            assertThat(viewModel.commentsList.getOrAwaitValue()).isEqualTo(
                listOf(TestDataUtils.comments[0])
            )
        }
    }

    @Test
    fun `댓글 개수를 업데이트한다`() = runTest {
        with(viewModel) {
            updateCommentsCount("2")
            assertThat(updateCommentsCount.data).isTrue()
        }
    }

    @Test
    fun `해당 댓글 작성된 모든 대댓글을 가져온다`() = runTest {
        with(viewModel) {
            getReComments(TestDataUtils.comments[0])
            assertThat(viewModel.recommentsList.getOrAwaitValue()).isEqualTo(
                listOf(TestDataUtils.recomments[1], TestDataUtils.recomments[0])
            )
        }
    }

    @Test
    fun `게시물 이미지를 가져온다`() = runTest {
        with(viewModel) {
            getPostKey("abs")
            getPostImages()
            assertThat(viewModel.postDetailImageList.getOrAwaitValue()).isEqualTo(
                arrayListOf(
                    PostModel.PostImage("abs", "0"),
                    PostModel.PostImage("abs", "1")
                )
            )
        }
    }
}