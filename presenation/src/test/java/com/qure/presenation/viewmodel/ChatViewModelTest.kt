package com.qure.presenation.viewmodel

import com.qure.domain.model.ChatRoom
import com.qure.presenation.BaseTest
import com.qure.presenation.CoroutinesTestExtension
import com.qure.presenation.InstantTaskExecutorExtension
import com.qure.presenation.getOrAwaitValue
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class, CoroutinesTestExtension::class)
class ChatViewModelTest : BaseTest() {

    private lateinit var viewModel: ChatViewModel

    override fun init() {
        viewModel = ChatViewModel(userUseCase, chatUseCase)
        viewModel.getAllChatRoom()
    }

    @Test
    fun `현재 유저의 채팅방 데이터를 모두 가져온다`() = runTest {
        assertThat(viewModel.chatRooms.getOrAwaitValue()).isEqualTo(
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
    fun `상대방 1대1 채팅방을 찾는다`() = runTest {
        val chatRoom = viewModel.findChatRoom("abs")
        assertThat(chatRoom).isEqualTo(
            ChatRoom(
                roomId = "abs",
                lastmsg = "abs",
                userCount = 2,
                users = arrayListOf("abs", "a"),
                unreadCount = mutableMapOf("abs" to 1, "a" to 1)
            )
        )
    }

    @Test
    fun `채팅방을 생성한다`() = runTest {
        viewModel.addChatRoom(ChatRoom(
            roomId = "b",
            lastmsg = "b",
            userCount = 2,
            users = arrayListOf("abs", "b"),
            unreadCount = mutableMapOf("abs" to 1, "b" to 1)
        ))
        assertThat(viewModel.addChatRoom.data).isTrue()
    }
}