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

    private fun createViewModel(
        threadId: String,
        repository: SpaceRepository,
    ): ThreadViewModel = ThreadViewModel(threadId = threadId, repository = repository)

    @Test
    fun `メッセージ一覧が取得でき、再読み込みもできる`() =
        runTest {
            val viewModel = createViewModel("thread-1", FakeSpaceRepository())

            viewModel.uiState.test {
                val initialState = awaitItem()
                initialState.threadMessages shouldBe emptyList()
                initialState.isLoading shouldBe false
                initialState.isError shouldBe false
                initialState.isRefreshing shouldBe false

                val loadingState = awaitItem()
                loadingState.threadMessages shouldBe emptyList()
                loadingState.isLoading shouldBe true
                loadingState.isError shouldBe false
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
                loadedState.isError shouldBe false
                loadedState.isRefreshing shouldBe false

                viewModel.refreshMessages()

                val refreshingState = awaitItem()
                refreshingState.isLoading shouldBe false
                refreshingState.isError shouldBe false
                refreshingState.isRefreshing shouldBe true

                val secondLoadingState = awaitItem()
                secondLoadingState.isLoading shouldBe true
                secondLoadingState.isError shouldBe false
                secondLoadingState.isRefreshing shouldBe true

                val refreshState = awaitItem()
                refreshState.threadMessages.size shouldBe 2
                refreshState.threadMessages[0].id shouldBe "msg-1"
                refreshState.threadMessages[0].body shouldBe "thread-1"
                refreshState.threadMessages[0].creator shouldBe Creator(name = "name1")
                refreshState.threadMessages[1].id shouldBe "msg-2"
                refreshState.threadMessages[1].body shouldBe "thread-2"
                refreshState.threadMessages[1].creator shouldBe Creator(name = "name2")
                refreshState.isLoading shouldBe false
                refreshState.isError shouldBe false
                refreshState.isRefreshing shouldBe false
            }
        }

    @Test
    fun `メッセージ一覧が取得できるが、再読み込みができない`() =
        runTest {
            val viewModel = createViewModel("thread-error", FakeSpaceRepository())

            viewModel.uiState.test {
                val initialState = awaitItem()
                initialState.threadMessages shouldBe emptyList()
                initialState.isLoading shouldBe false
                initialState.isError shouldBe false
                initialState.isRefreshing shouldBe false

                val loadingState = awaitItem()
                loadingState.threadMessages shouldBe emptyList()
                loadingState.isLoading shouldBe true
                loadingState.isError shouldBe false
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
                loadedState.isError shouldBe false
                loadedState.isRefreshing shouldBe false

                viewModel.refreshMessages()

                val refreshingState = awaitItem()
                refreshingState.isLoading shouldBe false
                refreshingState.isError shouldBe false
                refreshingState.isRefreshing shouldBe true

                val secondLoadingState = awaitItem()
                secondLoadingState.isLoading shouldBe true
                secondLoadingState.isError shouldBe false
                secondLoadingState.isRefreshing shouldBe true

                val refreshState = awaitItem()
                refreshState.threadMessages shouldBe emptyList()
                refreshState.isLoading shouldBe false
                refreshState.isError shouldBe true
                refreshState.isRefreshing shouldBe false
            }
        }

    @Test
    fun `メッセージ一覧が取得できない`() =
        runTest {
            val viewModel = createViewModel("", FakeSpaceRepositoryNoThreadMsg())

            viewModel.uiState.test {
                val initialState = awaitItem()
                initialState.threadMessages shouldBe emptyList()
                initialState.isLoading shouldBe false
                initialState.isError shouldBe false
                initialState.isRefreshing shouldBe false

                val loadingState = awaitItem()
                loadingState.threadMessages shouldBe emptyList()
                loadingState.isLoading shouldBe true
                loadingState.isError shouldBe false
                loadingState.isRefreshing shouldBe false

                val loadedState = awaitItem()
                loadedState.threadMessages shouldBe emptyList()
                loadedState.isLoading shouldBe false
                loadedState.isError shouldBe true
                loadedState.isRefreshing shouldBe false
            }
        }
}

// 初回正常系のRepository
private class FakeSpaceRepository : SpaceRepository {
    var loadCount = 0

    override suspend fun getAllThreads(spaceId: String): List<Thread> = emptyList()

    override suspend fun getMessagesForThread(threadId: String): List<ThreadMessage> {
        loadCount++ // 読み込み回数用のカウンタ

        return when {
            loadCount == 2 && threadId == "thread-error" -> throw Exception()
            loadCount == 1 || threadId == "thread-1" ->
                listOf(
                    ThreadMessage("msg-1", "thread-1", Creator("name1"), emptyList()),
                    ThreadMessage("msg-2", "thread-2", Creator("name2"), emptyList())
                )
            else -> emptyList()
        }
    }
}

// 初回異常系のRepository
private class FakeSpaceRepositoryNoThreadMsg : SpaceRepository {
    override suspend fun getAllThreads(spaceId: String): List<Thread> = emptyList()

    override suspend fun getMessagesForThread(threadId: String): List<ThreadMessage> = throw Exception()
    // 厳密な挙動を再現する。ViewModel側のExceptionにcatchされるために、単なるEmptyListではなくExceptionをthrowする
}
