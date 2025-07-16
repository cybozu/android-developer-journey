package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import com.cybozu.sample.kintone.spaces.data.space.KintoneMessage
import com.cybozu.sample.kintone.spaces.data.space.KintoneThread
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ThreadViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun shouldLoadMessagesForThread() {
        val repository = FakeSpaceRepositoryForThread()
        val viewModel = ThreadViewModel(repository)

        viewModel.loadMessages("thread-1")
        val messages = viewModel.messages.value

        messages.size shouldBe 2
        messages[0].id shouldBe "msg-1"
        messages[0].threadId shouldBe "thread-1"
    }

    @Test
    fun shouldProvideEmptyMessagesInitially() {
        val repository = FakeSpaceRepositoryForThread()
        val viewModel = ThreadViewModel(repository)

        viewModel.messages.value.shouldBeEmpty()
    }

    private class FakeSpaceRepositoryForThread : SpaceRepository {
        override fun getAllThreads(): List<KintoneThread> {
            return emptyList()
        }

        override fun getMessagesForThread(threadId: String): List<KintoneMessage> {
            if (threadId == "thread-1") {
                return listOf(
                    KintoneMessage("msg-1", "thread-1", "User1", "", "Message 1"),
                    KintoneMessage("msg-2", "thread-1", "User2", "", "Message 2")
                )
            }
            return emptyList()
        }
    }
}
