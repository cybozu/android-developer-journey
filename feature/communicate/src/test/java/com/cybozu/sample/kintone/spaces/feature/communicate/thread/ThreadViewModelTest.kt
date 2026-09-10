package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import app.cash.turbine.test
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
import com.cybozu.sample.kintone.spaces.data.space.entity.Creator
import com.cybozu.sample.kintone.spaces.data.space.entity.Thread
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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

    private fun createViewModel(shouldFail: (callCount: Int) -> Boolean = { false }): ThreadViewModel {
        val repository = FakeSpaceRepository(shouldFail = shouldFail)
        return ThreadViewModel(spaceId = "space-1", threadId = "thread-1", repository = repository)
    }

    @Test
    fun `メッセージ一覧が取得できる`() =
        runTest {
            val viewModel = createViewModel(shouldFail = { false })

            viewModel.uiState.test {
                val initialState = awaitItem()
                initialState.threadMessages shouldBe emptyList()
                initialState.isLoading shouldBe false
                initialState.isRefreshing shouldBe false

                val loadingState = awaitItem()
                loadingState.threadMessages shouldBe emptyList()
                loadingState.isLoading shouldBe true
                loadingState.isRefreshing shouldBe false

                val loadedState = awaitItem()
                loadedState.threadMessages.size shouldBe 2
                loadedState.threadMessages[0].id shouldBe "msg-1"
                loadedState.threadMessages[0].body shouldBe "thread-1"
                loadedState.threadMessages[0].creator shouldBe Creator(name = "name1")
                loadedState.threadMessages[1].id shouldBe "msg-2"
                loadedState.threadMessages[1].body shouldBe "thread-2"
                loadedState.threadMessages[1].creator shouldBe Creator(name = "name2")
                loadedState.isLoading shouldBe false
                loadedState.errorMessage shouldBe null
                loadedState.isRefreshing shouldBe false
            }
        }

    @Test
    fun `メッセージ取得に失敗したときエラー状態になる`() =
        runTest {
            val viewModel = createViewModel(shouldFail = { true })

            viewModel.uiState.test {
                val initialState = awaitItem()
                initialState.threadMessages shouldBe emptyList()
                initialState.isLoading shouldBe false
                initialState.isRefreshing shouldBe false

                val loadingState = awaitItem()
                loadingState.threadMessages shouldBe emptyList()
                loadingState.isLoading shouldBe true
                loadingState.isRefreshing shouldBe false

                val errorState = awaitItem()
                errorState.threadMessages shouldBe emptyList()
                errorState.errorMessage shouldNotBe null
                errorState.isLoading shouldBe false
                errorState.isRefreshing shouldBe false
            }
        }

    @Test
    fun `clearErrorMessageでerrorMessageがnullに戻る`() =
        runTest {
            val viewModel = createViewModel(shouldFail = { true })

            viewModel.uiState.test {
                skipItems(2) // initialState〜loadingState

                val errorState = awaitItem()
                errorState.errorMessage shouldNotBe null

                viewModel.clearErrorMessage()
                val clearedState = awaitItem()
                clearedState.errorMessage shouldBe null
            }
        }

    @Test
    fun `メッセージ一覧取得後、refreshでメッセージ一覧を再取得できる`() {
        runTest {
            val viewModel = createViewModel { false }

            viewModel.uiState.test {
                skipItems(3) // initialState,loadingState,loadedState

                viewModel.refresh()

                val refreshingState = awaitItem()
                refreshingState.threadMessages.size shouldBe 2
                refreshingState.isLoading shouldBe false
                refreshingState.isRefreshing shouldBe true

                val refreshedState = awaitItem()
                refreshedState.threadMessages.size shouldBe 2
                refreshedState.threadMessages[0].id shouldBe "msg-1"
                refreshedState.threadMessages[0].body shouldBe "thread-1"
                refreshedState.threadMessages[0].creator shouldBe Creator(name = "name1")
                refreshedState.threadMessages[1].id shouldBe "msg-2"
                refreshedState.threadMessages[1].body shouldBe "thread-2"
                refreshedState.threadMessages[1].creator shouldBe Creator(name = "name2")
                refreshedState.isLoading shouldBe false
                refreshedState.errorMessage shouldBe null
                refreshedState.isRefreshing shouldBe false
            }
        }
    }

    @Test
    fun `メッセージ一覧取得後、refreshでメッセージ取得失敗時にエラー状態になる`() {
        runTest {
            val viewModel = createViewModel(shouldFail = { count -> count == 2 })

            viewModel.uiState.test {
                skipItems(3) // initialState,loadingState,loadedState

                viewModel.refresh()

                val refreshingState = awaitItem()
                refreshingState.threadMessages.size shouldBe 2
                refreshingState.isLoading shouldBe false
                refreshingState.isRefreshing shouldBe true

                val refreshErrorState = awaitItem()
                refreshErrorState.threadMessages.size shouldBe 2
                refreshErrorState.errorMessage shouldNotBe null
                refreshErrorState.isLoading shouldBe false
                refreshErrorState.isRefreshing shouldBe false
            }
        }
    }

    @Test
    fun `メッセージ取得失敗後、refreshでメッセージ一覧を再取得できる`() {
        runTest {
            val viewModel = createViewModel(shouldFail = { count -> count == 1 })

            viewModel.uiState.test {
                skipItems(3) // initialState,loadingState,errorState

                viewModel.clearErrorMessage()
                awaitItem() // clearedState

                viewModel.refresh()

                val refreshingState = awaitItem()
                refreshingState.threadMessages shouldBe emptyList()
                refreshingState.isLoading shouldBe false
                refreshingState.isRefreshing shouldBe true

                val refreshedState = awaitItem()
                refreshedState.threadMessages.size shouldBe 2
                refreshedState.threadMessages[0].id shouldBe "msg-1"
                refreshedState.threadMessages[0].body shouldBe "thread-1"
                refreshedState.threadMessages[0].creator shouldBe Creator(name = "name1")
                refreshedState.threadMessages[1].id shouldBe "msg-2"
                refreshedState.threadMessages[1].body shouldBe "thread-2"
                refreshedState.threadMessages[1].creator shouldBe Creator(name = "name2")
                refreshedState.isLoading shouldBe false
                refreshedState.errorMessage shouldBe null
                refreshedState.isRefreshing shouldBe false
            }
        }
    }

    @Test
    fun `メッセージ取得失敗後、refreshでメッセージ取得失敗時にエラー状態になる`() {
        runTest {
            val viewModel = createViewModel(shouldFail = { true })

            viewModel.uiState.test {
                skipItems(3) // initialState,loadingState,errorState

                viewModel.clearErrorMessage()
                awaitItem() // clearedState

                viewModel.refresh()

                val refreshingState = awaitItem()
                refreshingState.threadMessages shouldBe emptyList()
                refreshingState.isLoading shouldBe false
                refreshingState.isRefreshing shouldBe true

                val refreshErrorState = awaitItem()
                refreshErrorState.threadMessages shouldBe emptyList()
                refreshErrorState.errorMessage shouldNotBe null
                refreshErrorState.isLoading shouldBe false
                refreshErrorState.isRefreshing shouldBe false
            }
        }
    }

    @Test
    fun `メッセージを投稿できる`() {
        runTest {
            val viewModel = createViewModel(shouldFail = { false })

            viewModel.uiState.test {
                skipItems(3) // initialState,loadingState,loadedState

                viewModel.postMessage("メッセージを投稿")

                val postingState = awaitItem()
                postingState.isPosting shouldBe true

                val postedState = awaitItem()
                postedState.isPosting shouldBe false
                postedState.errorMessage shouldBe null
            }
        }
    }

    @Test
    fun `メッセージ投稿に失敗したときエラー状態になる`() {
        runTest {
            val viewModel = createViewModel(shouldFail = { count -> count == 2 })

            viewModel.uiState.test {
                skipItems(3) // initialState,loadingState,loadedState

                viewModel.postMessage("メッセージを投稿")

                val postingState = awaitItem()
                postingState.isPosting shouldBe true

                val postErrorState = awaitItem()
                postErrorState.isPosting shouldBe false
                postErrorState.errorMessage shouldNotBe null
            }
        }
    }
}

private class FakeSpaceRepository(
    private val shouldFail: (callCount: Int) -> Boolean = { false },
) : SpaceRepository {
    private var callCount = 0

    override suspend fun getAllThreads(spaceId: String): List<Thread> = emptyList()

    override suspend fun getMessagesForThread(threadId: String): List<ThreadMessage> {
        callCount++
        if (shouldFail(callCount)) {
            throw IOException("メッセージ取得失敗")
        }
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

    override suspend fun addMessageForThread(
        space: String,
        thread: String,
        text: String,
    ) {
        callCount++
        if (shouldFail(callCount)) {
            throw IOException("メッセージ投稿失敗")
        }
    }
}
