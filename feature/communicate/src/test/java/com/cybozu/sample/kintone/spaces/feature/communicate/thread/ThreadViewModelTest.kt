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

    private fun createViewModel(shouldFail: Boolean = false): ThreadViewModel {
        val repository = FakeSpaceRepository(shouldFail = shouldFail)
        return ThreadViewModel(threadId = "thread-1", repository = repository)
    }

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
    fun `データ取得に失敗したときエラー状態になる`() =
        runTest {
            val viewModel = createViewModel(shouldFail = true)

            viewModel.uiState.test {
                // 初期:何もデータがない状態を確認
                val initialState = awaitItem()
                initialState.threadMessages shouldBe emptyList()
                initialState.isLoading shouldBe false
                initialState.errorMessage shouldBe null

                // loading:データ取得開始でローディング表示される状態を確認
                val loadingState = awaitItem()
                loadingState.threadMessages shouldBe emptyList()
                loadingState.isLoading shouldBe true
                loadingState.errorMessage shouldBe null

                // error:データ取得失敗でエラーメッセージが表示される状態を確認
                val errorState = awaitItem()
                errorState.threadMessages shouldBe emptyList()
                errorState.isLoading shouldBe false
                errorState.errorMessage shouldBe "メッセージを取得できませんでした\n原因: 不明なエラー"
            }
        }
}

private class FakeSpaceRepository(
    private val shouldFail: Boolean = false,
) : SpaceRepository {
    override suspend fun getAllThreads(spaceId: String): List<Thread> = emptyList()

    override suspend fun getMessagesForThread(threadId: String): Result<List<ThreadMessage>> =
        if (shouldFail) {
            Result.failure(RuntimeException("データ取得に失敗しました"))
        } else if (threadId == "thread-1") {
            Result.success(
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
        } else {
            Result.success(emptyList())
        }
}
