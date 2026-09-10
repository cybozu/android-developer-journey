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

    private fun createViewModel(repository: SpaceRepository = FakeSpaceRepository()): ThreadViewModel =
        ThreadViewModel(threadId = "thread-1", repository = repository)

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
    fun `更新すると最新のメッセージ一覧が表示される`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.uiState.test {
                val initialState = awaitItem()
                initialState.threadMessages shouldBe emptyList()
                initialState.isLoading shouldBe false
                initialState.isRefreshing shouldBe false

                val loadingState = awaitItem()
                loadingState.isLoading shouldBe true
                loadingState.isRefreshing shouldBe false

                val loadedState = awaitItem()
                loadedState.threadMessages.size shouldBe 2
                loadedState.isLoading shouldBe false
                loadedState.isRefreshing shouldBe false

                viewModel.refresh()

                val refreshingState = awaitItem()
                refreshingState.isRefreshing shouldBe true
                refreshingState.isLoading shouldBe false
                refreshingState.threadMessages.size shouldBe 2

                val refreshedState = awaitItem()
                refreshedState.isRefreshing shouldBe false
                refreshedState.isLoading shouldBe false
                refreshedState.threadMessages.size shouldBe 3
                refreshedState.threadMessages[2].id shouldBe "msg-3"
            }
        }

    @Test
    fun `メッセージ取得に失敗するとエラーになる`() =
        runTest {
            val viewModel = createViewModel(repository = FailingSpaceRepository())

            viewModel.uiState.test {
                val initialState = awaitItem()
                initialState.threadMessages shouldBe emptyList()
                initialState.isLoading shouldBe false

                val loadingState = awaitItem()
                loadingState.threadMessages shouldBe emptyList()
                loadingState.isLoading shouldBe true

                val errorState = awaitItem()
                errorState.hasError shouldBe true
                errorState.isLoading shouldBe false
            }
        }

    @Test
    fun `メッセージを投稿すると一覧に反映される`() =
        runTest {
            val viewModel = createViewModel(repository = PostingSpaceRepository())

            viewModel.uiState.test {
                awaitItem()
                awaitItem()
                val loadedState = awaitItem()
                loadedState.threadMessages.size shouldBe 1

                viewModel.updateInputText("新しいメッセージ")
                awaitItem().inputText shouldBe "新しいメッセージ"

                viewModel.postComment()

                val postingState = awaitItem()
                postingState.isPosting shouldBe true
                postingState.inputText shouldBe "新しいメッセージ"

                val postedState = awaitItem()
                postedState.isPosting shouldBe false
                postedState.inputText shouldBe ""

                val refreshingState = awaitItem()
                refreshingState.isRefreshing shouldBe true

                val refreshedState = awaitItem()
                refreshedState.isRefreshing shouldBe false
                refreshedState.threadMessages.size shouldBe 2
                refreshedState.threadMessages[1].body shouldBe "新しいメッセージ"
            }
        }
}

private class FakeSpaceRepository : SpaceRepository {
    private var callCount = 0

    override suspend fun getAllThreads(spaceId: String): List<Thread> = emptyList()

    override suspend fun getMessagesForThread(threadId: String): List<ThreadMessage> {
        callCount++

        if (threadId != "thread-1") return emptyList()

        val messages =
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
        if (callCount == 1) return messages

        return messages +
            ThreadMessage(
                id = "msg-3",
                body = "thread-3",
                creator = Creator(name = "name3"),
                comments = emptyList()
            )
    }

    override suspend fun postThreadComment(
        spaceId: String,
        threadId: String,
        text: String,
    ) = Unit
}

private class PostingSpaceRepository : SpaceRepository {
    private val postedTexts = mutableListOf<String>()

    override suspend fun getAllThreads(spaceId: String): List<Thread> = emptyList()

    override suspend fun getMessagesForThread(threadId: String): List<ThreadMessage> =
        listOf(
            ThreadMessage(
                id = "msg-1",
                body = "existing message",
                creator = Creator(name = "name1"),
                comments = emptyList()
            )
        ) +
            postedTexts.mapIndexed { index, text ->
                ThreadMessage(
                    id = "posted-${index + 1}",
                    body = text,
                    creator = Creator(name = "me"),
                    comments = emptyList()
                )
            }

    override suspend fun postThreadComment(
        spaceId: String,
        threadId: String,
        text: String,
    ) {
        postedTexts += text
    }
}

private class FailingSpaceRepository : SpaceRepository {
    override suspend fun getAllThreads(spaceId: String): List<Thread> = emptyList()

    override suspend fun getMessagesForThread(threadId: String): List<ThreadMessage> = throw RuntimeException("network error")

    override suspend fun postThreadComment(
        spaceId: String,
        threadId: String,
        text: String,
    ): Unit = throw RuntimeException("network error")
}
