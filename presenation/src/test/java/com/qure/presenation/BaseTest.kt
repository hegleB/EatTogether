package com.qure.presenation

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseUser
import com.qure.domain.usecase.*
import com.qure.presenation.data.fakes.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.junit.Before
import org.junit.Rule
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

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

    protected suspend fun <T> getValueOrThrow(
        liveData: LiveData<T>,
        postFunction: (() -> Any)? = null,
        timeout: Long = 3000
    ): T {
        var result: T? = null
        liveData.observeForever {
            result = it
        }

        if (postFunction != null) {
            val postJob = postFunction()
            if (postJob is Job) postJob.join()
        }
        delay(timeout)

        if (result == null) throw TimeoutException(ERROR_MESSAGE_LIVEDATA_NULL)
        else return result!!
    }

    companion object {
        const val ERROR_MESSAGE_LIVEDATA_NULL = "LiveData has null value"
    }
}