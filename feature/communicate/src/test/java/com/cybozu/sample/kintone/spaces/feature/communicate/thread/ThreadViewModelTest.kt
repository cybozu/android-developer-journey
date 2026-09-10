package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import app.cash.turbine.test
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
import com.cybozu.sample.kintone.spaces.data.space.entity.Creator
import com.cybozu.sample.kintone.spaces.data.space.entity.Thread
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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

    private fun createViewModel(
        getMessagesForThread: suspend (String) -> List<ThreadMessage> = { threadId ->
            delay(100) // 通信時間を模擬
            if (threadId == "thread-1") {
                listOf(
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
            } else {
                emptyList()
            }
        },
    ): ThreadViewModel =
        ThreadViewModel(threadId = "thread-1", repository = FakeSpaceRepository(getMessagesForThread))

    @Test
    fun `メッセージ一覧が取得できる`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.uiState.test {
                val initialState = awaitItem()
                initialState.threadMessages shouldBe emptyList()
                initialState.isLoading shouldBe false

                val loadingState = awaitItem()
                loadingState.threadMessages shouldBe emptyList()
                loadingState.isLoading shouldBe true

                val loadedState = awaitItem()
                loadedState.threadMessages.size shouldBe 2
                loadedState.threadMessages[0].id shouldBe "msg-1"
                loadedState.threadMessages[0].body shouldBe "thread-1"
                loadedState.threadMessages[0].creator shouldBe Creator(name = "name1")
                loadedState.threadMessages[1].id shouldBe "msg-2"
                loadedState.threadMessages[1].body shouldBe "thread-2"
                loadedState.threadMessages[1].creator shouldBe Creator(name = "name2")
                loadedState.isLoading shouldBe false
            }
        }

    @Test
    fun `メッセージ取得に失敗するとエラー状態になる`() =
        runTest {
            val viewModel =
                createViewModel(getMessagesForThread = { throw RuntimeException("test exception") })

            viewModel.uiState.test {
                val initialState = awaitItem()
                initialState.isLoading shouldBe false

                val loadingState = awaitItem()
                loadingState.isLoading shouldBe true

                val errorState = awaitItem()
                errorState.isLoading shouldBe false
                errorState.threadMessages shouldBe emptyList()
                errorState.errorMessageSeq shouldBe 1
            }
        }

    @Test
    fun `メッセージ取得がキャンセルされてもエラー状態にはならない`() =
        runTest {
            val viewModel =
                createViewModel(
                    getMessagesForThread = {
                        delay(100) // 通信時間を模擬
                        throw CancellationException("test cancellation")
                    }
                )

            viewModel.uiState.test {
                awaitItem() // 初期状態
                val loadingState = awaitItem()
                loadingState.isLoading shouldBe true

                advanceUntilIdle()
                expectNoEvents()
            }
        }

    @Test
    fun `リトライすると再度データ取得が行われる`() =
        runTest {
            var callCount = 0
            val viewModel =
                createViewModel(
                    getMessagesForThread = {
                        callCount++
                        if (callCount == 1) {
                            throw RuntimeException("test exception")
                        }
                        listOf(
                            ThreadMessage(
                                id = "msg-1",
                                body = "retry success",
                                creator = Creator(name = "name1"),
                                comments = emptyList()
                            )
                        )
                    }
                )

            viewModel.uiState.test {
                awaitItem()
                awaitItem()

                val errorState = awaitItem()
                errorState.errorMessageSeq shouldBe 1

                viewModel.onErrorMessageShown()
                val clearedState = awaitItem()
                clearedState.errorMessageSeq shouldBe null

                viewModel.onRetryClick()
                val retryLoadingState = awaitItem()
                retryLoadingState.isLoading shouldBe true

                val successState = awaitItem()
                successState.isLoading shouldBe false
                successState.threadMessages.size shouldBe 1
                successState.threadMessages[0].id shouldBe "msg-1"
                successState.errorMessageSeq shouldBe null
            }
        }
}

private class FakeSpaceRepository(
    private val getMessagesForThreadImpl: suspend (String) -> List<ThreadMessage>,
) : SpaceRepository {
    override suspend fun getAllThreads(spaceId: String): List<Thread> = emptyList()

    override suspend fun getMessagesForThread(threadId: String): List<ThreadMessage> = getMessagesForThreadImpl(threadId)
}
