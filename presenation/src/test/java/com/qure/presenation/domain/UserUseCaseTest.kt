package com.qure.presenation.domain

import com.qure.domain.model.User
import com.qure.presenation.BaseTest
import com.qure.presenation.CoroutinesTestExtension
import com.qure.presenation.InstantTaskExecutorExtension
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class, CoroutinesTestExtension::class)
class UserUseCaseTest : BaseTest() {


    override fun init() {

    }

    @Test
    fun `유저 정보를 설정한다`() = runTest {
        assertThat(userUseCase.setUser("abs", User("abs", "abs")).data).isEqualTo(true)
        assertThat(userUseCase.setUser("", User()).data).isEqualTo(false)
    }


    @Test
    fun `가입된 유저 정보를 가져온다`() = runTest {
        val expected = User("abs@abs.com", "abs", "abs")
        assertThat(userUseCase.getUser("abs").first().data).isEqualTo(expected)
        assertThat(userUseCase.getUser("").first().data).isEqualTo(User())
    }

    @Test
    fun `전체 유저 정보를 가져온다`() = runTest {
        val expected = listOf(
            User("abc@a.com", "abc","abc"),
            User("a@a.com", "a", "a"),
            User("abs@abs.com", "abs", "abs")
        )
        assertThat(userUseCase.getAllUser().first().data).isEqualTo(expected)
    }

    @Test
    fun `유저 정보를 업데이트 한다`() = runTest {
        assertThat(userUseCase.updateUser("abc", "abc","","").data).isEqualTo(true)
        assertThat(userUseCase.updateUser("abc1", "abc","","").data).isEqualTo(false)
    }
}