package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import app.cash.turbine.test
import com.cybozu.sample.kintone.spaces.data.space.KintoneMessage
import com.cybozu.sample.kintone.spaces.data.space.KintoneThread
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
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

    private fun createViewModel(): ThreadViewModel {
        val repository = FakeSpaceRepository()
        return ThreadViewModel(threadId = "thread-1", repository = repository)
    }

    @Test
    fun `メッセージ一覧が取得できる`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.messages.test {
                val messages = expectMostRecentItem()

                messages.size shouldBe 2
                messages[0].id shouldBe "msg-1"
                messages[0].threadId shouldBe "thread-1"
            }
        }
}

private class FakeSpaceRepository : SpaceRepository {
    override suspend fun getAllThreads(): List<KintoneThread> = emptyList()

    override suspend fun getMessagesForThread(threadId: String): List<KintoneMessage> {
        if (threadId == "thread-1") {
            return listOf(
                KintoneMessage("msg-1", "thread-1", "User1", "", "Message 1"),
                KintoneMessage("msg-2", "thread-1", "User2", "", "Message 2")
            )
        }
        return emptyList()
    }
}
