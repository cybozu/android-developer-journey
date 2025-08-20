package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import app.cash.turbine.test
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
import com.cybozu.sample.kintone.spaces.data.space.entity.Creator
import com.cybozu.sample.kintone.spaces.data.space.entity.Thread
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ThreadViewModelTest {
    @Before
    fun setup() {
        val testDispatcher = StandardTestDispatcher()
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
    fun testGetMessagesSuccess() =
        runTest {
            val viewModel = createViewModel()

            viewModel.uiState.test {
                val initialState = awaitItem()
                (initialState is ThreadUiState.Initial) shouldBe true

                val loadingState = awaitItem()
                (loadingState is ThreadUiState.Loading) shouldBe true

                val loadedState = awaitItem()
                (loadedState is ThreadUiState.Success) shouldBe true
                val successState = loadedState as ThreadUiState.Success
                successState.threadMessage.size shouldBe 2
                successState.threadMessage[0].id shouldBe "msg-1"
                successState.threadMessage[0].body shouldBe "thread-1"
                successState.threadMessage[0].creator shouldBe Creator(name = "name1")
                successState.threadMessage[1].id shouldBe "msg-2"
                successState.threadMessage[1].body shouldBe "thread-2"
                successState.threadMessage[1].creator shouldBe Creator(name = "name2")
            }
        }

    @Test
    fun testGetMessagesError() =
        runTest {
            val viewModel = ThreadViewModel(threadId = "thread-1", repository = ErrorFakeSpaceRepository())

            viewModel.uiState.test {
                val initialState = awaitItem()
                (initialState is ThreadUiState.Initial) shouldBe true

                val loadingState = awaitItem()
                (loadingState is ThreadUiState.Loading) shouldBe true

                val errorState = awaitItem().shouldBeInstanceOf<ThreadUiState.Error>()
                errorState.errorMessage shouldBe "メッセージが取得できませんでした"
            }
        }

    @Test
    fun refreshMessageSuccess() =
        runTest {
            val viewModel = createViewModel()

            viewModel.uiState.test {
                val initialState = awaitItem()
                (initialState is ThreadUiState.Initial) shouldBe true

                val loadingState = awaitItem()
                (loadingState is ThreadUiState.Loading) shouldBe true

                val loadedState = awaitItem()
                (loadedState is ThreadUiState.Success)

                viewModel.refreshMessages()

                val refreshLoadingState = awaitItem()
                (refreshLoadingState is ThreadUiState.Loading) shouldBe true

                val refreshLoadedState = awaitItem()
                (refreshLoadedState is ThreadUiState.Success) shouldBe true
                val successState = refreshLoadedState as ThreadUiState.Success
                successState.threadMessage.size shouldBe 2
                successState.threadMessage[0].id shouldBe "msg-1"
                successState.threadMessage[0].body shouldBe "thread-1"
                successState.threadMessage[0].creator shouldBe Creator(name = "name1")
                successState.threadMessage[1].id shouldBe "msg-2"
                successState.threadMessage[1].body shouldBe "thread-2"
                successState.threadMessage[1].creator shouldBe Creator(name = "name2")

                cancelAndIgnoreRemainingEvents()
            }
        }
}

private class FakeSpaceRepository : SpaceRepository {
    override suspend fun getAllThreads(spaceId: String): List<Thread> = emptyList()

    override suspend fun getMessagesForThread(threadId: String): List<ThreadMessage> {
        if (threadId == "thread-1") {
            return listOf(
                ThreadMessage(
                    id = "msg-1",
                    body = "thread-1",
                    creator = Creator(name = "name1"),
                    comments = emptyList()
                ),
                ThreadMessage(
                    id = "msg-2",
                    body = "thread-2",
                    creator = Creator(name = "name2"),
                    comments = emptyList()
                )
            )
        }
        return emptyList()
    }
}

private class ErrorFakeSpaceRepository : SpaceRepository {
    override suspend fun getAllThreads(spaceId: String): List<Thread> = emptyList()

    override suspend fun getMessagesForThread(threadId: String): List<ThreadMessage> = throw IOException("ネットワークエラー")
}
