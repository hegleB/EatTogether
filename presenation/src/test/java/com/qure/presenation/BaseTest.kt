package com.qure.presenation

import com.google.firebase.auth.FirebaseUser
import com.qure.domain.usecase.*
import com.qure.presenation.data.fakes.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule

abstract class BaseTest {
    @ExperimentalCoroutinesApi
    @get: Rule
    val mainDispatcherRule = MainDispatcherRule()

    lateinit var fakeAuthRepositoryImpl: FakeAuthRepositoryImpl
    lateinit var fakeUserRepositoryImpl: FakeUserRepositoryImpl
    lateinit var fakeBarcodeRepositoryImpl: FakeBarcodeRepositoryImpl
    lateinit var fakeChatRepositoryImpl: FakeChatRepositoryImpl
    lateinit var fakeCommentRepositoryImpl: FakeCommentRepositoryImpl
    lateinit var fakeMeetingRepositoryImpl: FakeMeetingRepositoryImpl
    lateinit var fakePostRepositoryImpl: FakePostRepositoryImpl
    lateinit var fakeSettingRepositoryImpl: FakeSettingRepositoryImpl
    lateinit var userUseCase: UserUseCase
    lateinit var postUseCase: PostUseCase
    lateinit var authUseCase: AuthUseCase
    lateinit var barcodeUseCase: BarcodeUseCase
    lateinit var chatUseCase: ChatUseCase
    lateinit var commentUseCase: CommentUseCase
    lateinit var firebaseUser: FirebaseUser

    @Before
    fun setup() {
        fakeAuthRepositoryImpl = FakeAuthRepositoryImpl()
        fakeUserRepositoryImpl = FakeUserRepositoryImpl()
        fakeBarcodeRepositoryImpl = FakeBarcodeRepositoryImpl()
        fakeChatRepositoryImpl = FakeChatRepositoryImpl()
        fakeCommentRepositoryImpl = FakeCommentRepositoryImpl()
        fakeMeetingRepositoryImpl = FakeMeetingRepositoryImpl()
        fakePostRepositoryImpl = FakePostRepositoryImpl()
        fakeSettingRepositoryImpl = FakeSettingRepositoryImpl()
        userUseCase = UserUseCase(fakeUserRepositoryImpl, fakeAuthRepositoryImpl, fakeMeetingRepositoryImpl)
        postUseCase = PostUseCase(fakePostRepositoryImpl)
        authUseCase = AuthUseCase(fakeAuthRepositoryImpl)
        barcodeUseCase = BarcodeUseCase(fakeBarcodeRepositoryImpl)
        chatUseCase = ChatUseCase(fakeChatRepositoryImpl)
        commentUseCase = CommentUseCase(fakeCommentRepositoryImpl)
        init()
        firebaseUser = initFirebaseUser()
    }

    private fun initFirebaseUser(): FirebaseUser {
        val firebaseUser = mockk<FirebaseUser>()
        every { firebaseUser.uid } returns "abc"
        every { firebaseUser.email } returns "abc@a.com"
        return firebaseUser
    }

    abstract fun init()
}