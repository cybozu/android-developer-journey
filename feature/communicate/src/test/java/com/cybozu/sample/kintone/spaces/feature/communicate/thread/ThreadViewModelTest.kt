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

                val loadingState = awaitItem()
                loadingState.threadMessages shouldBe emptyList()
                loadingState.isLoading shouldBe true
                loadingState.isError shouldBe false

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

                viewModel.refreshMessages()

                val secondLoadingState = awaitItem()
                secondLoadingState.isLoading shouldBe true
                secondLoadingState.isError shouldBe false

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

                val loadingState = awaitItem()
                loadingState.threadMessages shouldBe emptyList()
                loadingState.isLoading shouldBe true
                loadingState.isError shouldBe false

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

                viewModel.refreshMessages()

                val secondLoadingState = awaitItem()
                secondLoadingState.isLoading shouldBe true
                secondLoadingState.isError shouldBe false

                val refreshState = awaitItem()
                refreshState.threadMessages shouldBe emptyList()
                refreshState.isLoading shouldBe false
                refreshState.isError shouldBe true
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

                val loadingState = awaitItem()
                loadingState.threadMessages shouldBe emptyList()
                loadingState.isLoading shouldBe true
                loadingState.isError shouldBe false

                val loadedState = awaitItem()
                loadedState.threadMessages shouldBe emptyList()
                loadedState.isLoading shouldBe false
                loadedState.isError shouldBe true
            }
        }
}

// 初回正常系のRepository
private class FakeSpaceRepository : SpaceRepository {
    var cnt = 0

    override suspend fun getAllThreads(spaceId: String): List<Thread> = emptyList()

    override suspend fun getMessagesForThread(threadId: String): List<ThreadMessage> {
        cnt++ // 読み込み回数用のカウンタ

        if (cnt == 2 && threadId == "thread-error") {
            throw Exception()
        } else if (cnt == 1 || threadId == "thread-1") {
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

// 初回異常系のRepository
private class FakeSpaceRepositoryNoThreadMsg : SpaceRepository {
    override suspend fun getAllThreads(spaceId: String): List<Thread> = emptyList()

    override suspend fun getMessagesForThread(threadId: String): List<ThreadMessage> = throw Exception()
    // 厳密な挙動を再現する。ViewModel側のExceptionにcatchされるために、単なるEmptyListではなくExceptionをthrowする
}
