package com.qure.presenation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.qure.domain.model.User
import com.qure.presenation.BaseTest
import com.qure.presenation.CoroutinesTestExtension
import com.qure.presenation.InstantTaskExecutorExtension
import com.qure.presenation.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class, CoroutinesTestExtension::class)
class PeopleViewModelTest : BaseTest() {

    private lateinit var viewModel: PeopleViewModel

    override fun init() {
        viewModel = PeopleViewModel(userUseCase, postUseCase, barcodeUseCase, uploadUseCase)
    }

    @Test
    fun `전체 유저 정보를 가져온다`() = runTest {
        with(viewModel) {
            getAllUser(User("abc@a.com", "abc", "abc"))
            assertThat(userList.getOrAwaitValue()).isEqualTo(
                listOf(User(
                    userid = "a@a.com",
                    uid = "a",
                    usernm = "a",
                ),
                User(
                    userid = "abs@abs.com",
                    uid = "abs",
                    usernm = "abs",
                )))
        }
    }

    @Test
    fun `현재 유저 정보를 가져온다`() = runTest {
        with(viewModel) {
            getUserInfo("abc")
            assertThat(user.getOrAwaitValue()).isEqualTo(User("abc@a.com", "abc", "abc"))
        }
    }

    @Test
    fun `유저 게시물 갯수를 가져온다`() = runTest {
        with(viewModel) {
            getPostCount("abs")
            assertThat(postCount.getOrAwaitValue()).isEqualTo("2")
        }
    }

    @Test
    fun `유저 미팅 횟수를 가져온다`() = runTest {
        with(viewModel) {
            getMeetingCount("abs")
            assertThat(meetingCount.getOrAwaitValue()).isEqualTo("1")
        }
    }

    @Test
    fun `유저 좋아요 갯수를 가져온다`() = runTest {
        with(viewModel) {
            getLikeCount("abs")
            assertThat(likeCount.getOrAwaitValue()).isEqualTo("2")
        }
    }
}