package com.qure.presenation.domain

import com.qure.domain.model.ChatMessage
import com.qure.domain.model.ChatRoom
import com.qure.presenation.BaseTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ChatUseCaseTest : BaseTest() {
    override fun init() {

    }

    @Test
    fun `모든 채팅방 정보를 가져온다`() = runTest {
        assertThat(chatUseCase.getAllChatRoom("abs").first().data).isEqualTo(
            listOf(
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
        )
    }

    @Test
    fun `채팅방을 추가한다`() = runTest {
        assertThat(
            chatUseCase.setChatRoom(
                ChatRoom(
                    roomId = "b",
                    lastmsg = "b",
                    userCount = 2,
                    users = arrayListOf("abs", "b"),
                    unreadCount = mutableMapOf("abs" to 0, "b" to 0)
                )
            ).data
        ).isTrue()
    }

    @Test
    fun `해당 채팅방의 모든 메시지를 가져온다`() = runTest {
        assertThat(
            chatUseCase.getAllMessage("a").first().data
        ).isEqualTo(
            listOf(
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
        )
    }

    @Test
    fun `메시지를 전송한다`() = runTest {
        assertThat(
            chatUseCase.setChatMessage(
                ChatMessage(
                    roomId = "a",
                    message = "abcd",
                    timestamp = "0"
                )
            ).data
        ).isTrue
    }

    @Test
    fun `채팅방의 메시지를 읽는다`() = runTest {
        assertThat(
            chatUseCase.updateChatRoom("abs", mutableMapOf("abs" to 0, "a" to 0)).data
        ).isTrue()
    }
}