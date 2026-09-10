package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import app.cash.turbine.test
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
import com.cybozu.sample.kintone.spaces.data.space.entity.Creator
import com.cybozu.sample.kintone.spaces.data.space.entity.Thread
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage
import io.kotest.matchers.shouldBe
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

    private fun createViewModel(shouldThrowOnGetMessages: Boolean = false): Pair<ThreadViewModel, FakeSpaceRepository> {
        val repository = FakeSpaceRepository(shouldThrowOnGetMessages = shouldThrowOnGetMessages)
        return ThreadViewModel(threadId = "thread-1", repository = repository) to repository
    }

    @Test
    fun `メッセージ一覧が取得できる`() =
        runTest {
            val (viewModel) = createViewModel()

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
    fun `メッセージ取得に失敗した場合エラーメッセージがセットされる`() =
        runTest {
            val (viewModel) = createViewModel(shouldThrowOnGetMessages = true)

            viewModel.uiState.test {
                val initialState = awaitItem()
                initialState.errorMessage shouldBe null
                initialState.isLoading shouldBe false

                val loadingState = awaitItem()
                loadingState.isLoading shouldBe true
                loadingState.errorMessage shouldBe null

                val errorState = awaitItem()
                errorState.isLoading shouldBe false
                errorState.threadMessages shouldBe emptyList()
                errorState.errorMessage shouldBe "メッセージを取得できませんでした"
            }
        }

    @Test
    fun `更新すると最新のメッセージ一覧が表示される`() =
        runTest {
            val (viewModel) = createViewModel()

            viewModel.uiState.test {
                // 初期状態・ローディング中・初回読み込み完了の3件は、今回の検証に不要なので読み飛ばす
                skipItems(3)

                viewModel.refreshMessages()

                val refreshingState = awaitItem()
                refreshingState.isRefreshing shouldBe true

                val refreshedState = awaitItem()
                refreshedState.isRefreshing shouldBe false
                refreshedState.threadMessages.size shouldBe 2
                refreshedState.threadMessages[0].id shouldBe "msg-1"
                refreshedState.threadMessages[0].body shouldBe "thread-1"
                refreshedState.threadMessages[0].creator shouldBe Creator(name = "name1")
                refreshedState.threadMessages[1].id shouldBe "msg-2"
                refreshedState.threadMessages[1].body shouldBe "thread-2"
                refreshedState.threadMessages[1].creator shouldBe Creator(name = "name2")
                refreshedState.isLoading shouldBe false
            }
        }

    @Test
    fun `更新に失敗した場合エラーメッセージがセットされ元の一覧が維持される`() =
        runTest {
            val (viewModel, repository) = createViewModel()

            viewModel.uiState.test {
                // 初期状態・ローディング中・初回読み込み完了の3件は、今回の検証に不要なので読み飛ばす
                skipItems(3)

                repository.shouldThrowOnGetMessages = true

                viewModel.refreshMessages()

                val refreshingState = awaitItem()
                refreshingState.isRefreshing shouldBe true

                val errorRefreshState = awaitItem()
                errorRefreshState.isRefreshing shouldBe false
                errorRefreshState.threadMessages.size shouldBe 2
                errorRefreshState.threadMessages[0].id shouldBe "msg-1"
                errorRefreshState.threadMessages[0].body shouldBe "thread-1"
                errorRefreshState.threadMessages[0].creator shouldBe Creator(name = "name1")
                errorRefreshState.threadMessages[1].id shouldBe "msg-2"
                errorRefreshState.threadMessages[1].body shouldBe "thread-2"
                errorRefreshState.threadMessages[1].creator shouldBe Creator(name = "name2")
                errorRefreshState.errorMessage shouldBe "メッセージを取得できませんでした"
            }
        }

    @Test
    fun `メッセージを送信すると一覧に反映される`() =
        runTest {
            val (viewModel, repository) = createViewModel()

            viewModel.uiState.test {
                // 初期状態・ローディング中・初回読み込み完了の3件は、今回の検証に不要なので読み飛ばす
                skipItems(3)

                viewModel.onInputTextChanged("hogehoge")
                viewModel.onSendMessage()

                val inputTextState = awaitItem()
                inputTextState.inputText shouldBe "hogehoge"

                val clearTextState = awaitItem()
                clearTextState.inputText shouldBe ""

                val refreshingState = awaitItem()
                refreshingState.isRefreshing shouldBe true

                val refreshedState = awaitItem()
                refreshedState.isRefreshing shouldBe false
                refreshedState.threadMessages.size shouldBe 3
                refreshedState.threadMessages[0].id shouldBe "msg-1"
                refreshedState.threadMessages[0].body shouldBe "thread-1"
                refreshedState.threadMessages[0].creator shouldBe Creator(name = "name1")
                refreshedState.threadMessages[1].id shouldBe "msg-2"
                refreshedState.threadMessages[1].body shouldBe "thread-2"
                refreshedState.threadMessages[1].creator shouldBe Creator(name = "name2")
                refreshedState.threadMessages[2].id shouldBe "msg-3"
                refreshedState.threadMessages[2].body shouldBe "hogehoge"
                refreshedState.threadMessages[2].creator shouldBe Creator(name = "name3")
            }
        }
}

private class FakeSpaceRepository(
    var shouldThrowOnGetMessages: Boolean = false,
) : SpaceRepository {
    private val list = mutableListOf<ThreadMessage>()

    override suspend fun getAllThreads(spaceId: String): List<Thread> = emptyList()

    override suspend fun getMessagesForThread(threadId: String): List<ThreadMessage> {
        if (shouldThrowOnGetMessages) {
            throw RuntimeException("network error")
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
            ) + list
        }
        return emptyList()
    }

    override suspend fun postMessage(
        spaceId: String,
        threadId: String,
        body: String,
    ) {
        val threadMessage = ThreadMessage(id = "msg-3", body = body, creator = Creator(name = "name3"), comments = emptyList())
        list.add(threadMessage)
    }
}
