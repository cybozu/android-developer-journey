@file:Suppress("NonAsciiCharacters", "RemoveRedundantBackticks")
package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import app.cash.turbine.test
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
import com.cybozu.sample.kintone.spaces.data.space.entity.Creator
import com.cybozu.sample.kintone.spaces.data.space.entity.Thread
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage
import com.cybozu.sample.kintone.spaces.feature.communicate.R
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.instanceOf
import io.kotest.matchers.types.shouldBeInstanceOf
import java.io.IOException
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
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

    private fun createViewModel(getMessagesForThreadMock: (String) -> Result<List<ThreadMessage>>): ThreadViewModel {
        val repository =
            FakeSpaceRepository(
                getMessagesForThreadMock = getMessagesForThreadMock
            )
        return ThreadViewModel(threadId = "thread-1", repository = repository)
    }

    @Test
    fun `メッセージ一覧が取得できる`() =
        runTest {
            val viewModel =
                createViewModel(
                    getMessagesForThreadMock = {
                        success(
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
                        )
                    }
                )

            viewModel.uiState.test {
                val initialState = awaitItem()
                initialState shouldBe instanceOf<ThreadUiState.Idle>()

                val loadingState = awaitItem()
                loadingState shouldBe instanceOf<ThreadUiState.Loading>()

                val loadedState = awaitItem()
                loadedState.shouldBeInstanceOf<ThreadUiState.Success> {
                    it.threadMessages.size shouldBe 2
                    it.threadMessages[0].id shouldBe "msg-1"
                    it.threadMessages[0].body shouldBe "thread-1"
                    it.threadMessages[0].creator shouldBe Creator(name = "name1")
                    it.threadMessages[1].id shouldBe "msg-2"
                    it.threadMessages[1].body shouldBe "thread-2"
                    it.threadMessages[1].creator shouldBe Creator(name = "name2")
                }
            }
        }

    @Test
    fun `メッセージ一覧の取得に失敗`() =
        runTest {
            val viewModel =
                createViewModel(
                    getMessagesForThreadMock = {
                        failure(IOException())
                    }
                )

            viewModel.uiState.test {
                val initialState = awaitItem()
                initialState shouldBe instanceOf<ThreadUiState.Idle>()

                val loadingState = awaitItem()
                loadingState shouldBe instanceOf<ThreadUiState.Loading>()

                val loadedState = awaitItem()
                loadedState.shouldBeInstanceOf<ThreadUiState.Error> {
                    it.messageId shouldBe R.string.thread_error
                }
            }
        }

    @Test
    fun `メッセージを一覧を更新できる`() =
        runTest {
            var callCount = 0
            val viewModel =
                createViewModel(getMessagesForThreadMock = {
                    callCount++
                    if (callCount == 1) {
                        success(
                            listOf(
                                ThreadMessage(
                                    id = "msg-1",
                                    body = "thread-1",
                                    creator = Creator(name = "name1"),
                                    comments = emptyList()
                                )
                            )
                        )
                    } else {
                        success(
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
                        )
                    }
                })

            viewModel.uiState.test {
                skipItems(2)

                val loadedState = awaitItem()
                loadedState.shouldBeInstanceOf<ThreadUiState.Success> {
                    it.threadMessages.size shouldBe 1
                }
                viewModel.refreshMessages()

                val loadingState = awaitItem()
                loadingState shouldBe instanceOf<ThreadUiState.Refreshing>()

                val refreshedState = awaitItem()
                refreshedState.shouldBeInstanceOf<ThreadUiState.Success> {
                    it.threadMessages.size shouldBe 2
                    it.threadMessages[0].id shouldBe "msg-1"
                    it.threadMessages[0].body shouldBe "thread-1"
                    it.threadMessages[0].creator shouldBe Creator(name = "name1")
                    it.threadMessages[1].id shouldBe "msg-2"
                    it.threadMessages[1].body shouldBe "thread-2"
                    it.threadMessages[1].creator shouldBe Creator(name = "name2")
                }
            }
        }

    @Test
    fun `メッセージ一覧の更新に失敗`() =
        runTest {
            val viewModel =
                createViewModel(
                    getMessagesForThreadMock = {
                        failure(IOException())
                    }
                )

            viewModel.uiState.test {
                skipItems(3)
                viewModel.refreshMessages()

                val loadingState = awaitItem()
                loadingState shouldBe instanceOf<ThreadUiState.Refreshing>()

                val loadedState = awaitItem()
                loadedState.shouldBeInstanceOf<ThreadUiState.Error> {
                    it.messageId shouldBe R.string.thread_error
                }
            }
        }
}

private class FakeSpaceRepository(
    private val getMessagesForThreadMock: (String) -> Result<List<ThreadMessage>>,
) : SpaceRepository {
    override suspend fun getAllThreads(spaceId: String): List<Thread> = emptyList()

    override suspend fun getMessagesForThread(threadId: String): Result<List<ThreadMessage>> = getMessagesForThreadMock(threadId)
}
