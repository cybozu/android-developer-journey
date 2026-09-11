package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import app.cash.turbine.skipItems
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
        postMessage: suspend (String, String, String) -> Unit = { _, _, _ -> delay(100) }, // 通信時間を模擬
    ): ThreadViewModel =
        ThreadViewModel(
            spaceId = "space-1",
            threadId = "thread-1",
            repository = FakeSpaceRepository(getMessagesForThread, postMessage)
        )

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
                awaitItem()
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

    @Test
    fun `更新すると最新のメッセージ一覧が表示される`() =
        runTest {
            var callCount = 0
            val viewModel =
                createViewModel(
                    getMessagesForThread = {
                        callCount++
                        listOf(
                            ThreadMessage(
                                id = if (callCount == 1) "msg-1" else "msg-2",
                                body = "body",
                                creator = Creator(name = "name1"),
                                comments = emptyList()
                            )
                        )
                    }
                )

            viewModel.uiState.test {
                awaitItem()
                awaitItem()

                val loadedState = awaitItem()
                loadedState.threadMessages.size shouldBe 1
                loadedState.threadMessages[0].id shouldBe "msg-1"

                viewModel.onRefresh()
                val refreshingState = awaitItem()
                refreshingState.isRefreshing shouldBe true

                val refreshedState = awaitItem()
                refreshedState.isRefreshing shouldBe false
                refreshedState.threadMessages.size shouldBe 1
                refreshedState.threadMessages[0].id shouldBe "msg-2"
            }
        }

    @Test
    fun `更新に失敗するとエラー状態になり、更新前の一覧が保持される`() =
        runTest {
            var callCount = 0
            val viewModel =
                createViewModel(
                    getMessagesForThread = {
                        callCount++
                        if (callCount == 1) {
                            listOf(
                                ThreadMessage(
                                    id = "msg-1",
                                    body = "body",
                                    creator = Creator(name = "name1"),
                                    comments = emptyList()
                                )
                            )
                        } else {
                            throw RuntimeException("test exception")
                        }
                    }
                )

            viewModel.uiState.test {
                awaitItem()
                awaitItem()

                val loadedState = awaitItem()
                loadedState.threadMessages.size shouldBe 1

                viewModel.onRefresh()
                val refreshingState = awaitItem()
                refreshingState.isRefreshing shouldBe true

                val errorState = awaitItem()
                errorState.isRefreshing shouldBe false
                errorState.errorMessageSeq shouldBe 1
                errorState.threadMessages.size shouldBe 1
                errorState.threadMessages[0].id shouldBe "msg-1"
            }
        }

    @Test
    fun `作成ボタンを押すと入力欄が開き、閉じるボタンを押すと閉じて入力内容が破棄される`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.uiState.test {
                skipItems(3) // 初期状態→ロード中→ロード完了までスキップ

                viewModel.onComposeClick()
                val openedState = awaitItem()
                openedState.isComposerOpen shouldBe true

                viewModel.onInputTextChange("下書き")
                val typedState = awaitItem()
                typedState.inputText shouldBe "下書き"

                viewModel.onCloseComposeClick()
                val closedState = awaitItem()
                closedState.isComposerOpen shouldBe false
                closedState.inputText shouldBe ""
            }
        }

    @Test
    fun `入力が空の場合は送信されない`() =
        runTest {
            var postCallCount = 0
            val viewModel =
                createViewModel(
                    postMessage = { _, _, _ -> postCallCount++ }
                )

            viewModel.uiState.test {
                skipItems(3) // 初期状態→ロード中→ロード完了までスキップ

                viewModel.onSendClick()

                advanceUntilIdle()
                expectNoEvents()
            }
            postCallCount shouldBe 0
        }

    @Test
    fun `メッセージを送信すると一覧が再取得され、入力欄が閉じる`() =
        runTest {
            var getCallCount = 0
            val viewModel =
                createViewModel(
                    getMessagesForThread = {
                        getCallCount++
                        delay(100) // 通信時間を模擬
                        listOf(
                            ThreadMessage(
                                id = if (getCallCount == 1) "msg-1" else "msg-2",
                                body = "body",
                                creator = Creator(name = "name1"),
                                comments = emptyList()
                            )
                        )
                    }
                )

            viewModel.uiState.test {
                awaitItem()
                awaitItem()

                val loadedState = awaitItem()
                loadedState.threadMessages[0].id shouldBe "msg-1"

                viewModel.onComposeClick()
                awaitItem()

                viewModel.onInputTextChange("新しいメッセージ")
                awaitItem()

                viewModel.onSendClick()
                val sendingState = awaitItem()
                sendingState.isSending shouldBe true

                val sentState = awaitItem()
                sentState.isSending shouldBe false
                sentState.inputText shouldBe ""
                sentState.isComposerOpen shouldBe false

                val reloadedState = awaitItem()
                reloadedState.threadMessages.size shouldBe 1
                reloadedState.threadMessages[0].id shouldBe "msg-2"
                // 送信後の再取得ではPull to Refreshのインジケータを出さない
                reloadedState.isLoading shouldBe false
                reloadedState.isRefreshing shouldBe false
            }
        }

    @Test
    fun `メッセージの送信に失敗すると入力内容を保持したままエラー状態になる`() =
        runTest {
            val viewModel =
                createViewModel(
                    postMessage = { _, _, _ ->
                        delay(100) // 通信時間を模擬
                        throw RuntimeException("test exception")
                    }
                )

            viewModel.uiState.test {
                skipItems(3) // 初期状態→ロード中→ロード完了までスキップ

                viewModel.onComposeClick()
                awaitItem()

                viewModel.onInputTextChange("送れなかったメッセージ")
                awaitItem()

                viewModel.onSendClick()
                val sendingState = awaitItem()
                sendingState.isSending shouldBe true

                val errorState = awaitItem()
                errorState.isSending shouldBe false
                errorState.sendErrorMessageSeq shouldBe 1
                errorState.inputText shouldBe "送れなかったメッセージ"
                errorState.isComposerOpen shouldBe true
            }
        }

    @Test
    fun `メッセージの送信がキャンセルされてもエラー状態にはならない`() =
        runTest {
            val viewModel =
                createViewModel(
                    postMessage = { _, _, _ ->
                        delay(100) // 通信時間を模擬
                        throw CancellationException("test cancellation")
                    }
                )

            viewModel.uiState.test {
                skipItems(3) // 初期状態→ロード中→ロード完了までスキップ

                viewModel.onComposeClick()
                awaitItem()

                viewModel.onInputTextChange("text")
                awaitItem()

                viewModel.onSendClick()
                val sendingState = awaitItem()
                sendingState.isSending shouldBe true

                advanceUntilIdle()
                expectNoEvents()
            }
        }

    @Test
    fun `送信エラーのメッセージを表示し終えるとエラー状態がクリアされる`() =
        runTest {
            val viewModel =
                createViewModel(
                    postMessage = { _, _, _ -> throw RuntimeException("test exception") }
                )

            viewModel.uiState.test {
                skipItems(3) // 初期状態→ロード中→ロード完了までスキップ

                viewModel.onComposeClick()
                awaitItem()

                viewModel.onInputTextChange("text")
                awaitItem()

                viewModel.onSendClick()
                awaitItem()

                val errorState = awaitItem()
                errorState.sendErrorMessageSeq shouldBe 1

                viewModel.onSendErrorMessageShown()
                val clearedState = awaitItem()
                clearedState.sendErrorMessageSeq shouldBe null
            }
        }
}

private class FakeSpaceRepository(
    private val getMessagesForThreadImpl: suspend (String) -> List<ThreadMessage>,
    private val postMessageImpl: suspend (String, String, String) -> Unit = { _, _, _ -> },
) : SpaceRepository {
    override suspend fun getAllThreads(spaceId: String): List<Thread> = emptyList()

    override suspend fun getMessagesForThread(threadId: String): List<ThreadMessage> = getMessagesForThreadImpl(threadId)

    override suspend fun postMessage(
        spaceId: String,
        threadId: String,
        text: String,
    ) {
        postMessageImpl(spaceId, threadId, text)
    }
}
