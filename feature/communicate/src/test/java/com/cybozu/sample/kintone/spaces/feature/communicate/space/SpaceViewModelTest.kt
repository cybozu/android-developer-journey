package com.cybozu.sample.kintone.spaces.feature.communicate.space

import app.cash.turbine.test
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
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
class SpaceViewModelTest {
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
        getAllThreads: suspend (String) -> List<Thread> = {
            delay(100) // 通信時間を模擬
            listOf(
                Thread("thread-1", "space1", "Test Thread 1", "Last message 1"),
                Thread("thread-2", "space2", "Test Thread 2", "Last message 2")
            )
        },
    ): SpaceViewModel = SpaceViewModel(FakeSpaceRepository(getAllThreads))

    @Test
    fun `スレッド一覧が取得できる`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.uiState.test {
                val initialState = awaitItem()
                initialState.threads shouldBe emptyList()
                initialState.isLoading shouldBe false

                val loadingState = awaitItem()
                loadingState.threads shouldBe emptyList()
                loadingState.isLoading shouldBe true

                val loadedState = awaitItem()
                loadedState.threads.size shouldBe 2
                loadedState.threads[0].id shouldBe "thread-1"
                loadedState.threads[0].name shouldBe "Test Thread 1"
                loadedState.threads[1].id shouldBe "thread-2"
                loadedState.threads[1].name shouldBe "Test Thread 2"
                loadedState.isLoading shouldBe false
            }
        }

    @Test
    fun `スレッド一覧取得に失敗するとエラー状態になる`() =
        runTest {
            val viewModel = createViewModel(getAllThreads = { throw RuntimeException("test exception") })

            viewModel.uiState.test {
                val initialState = awaitItem()
                initialState.isLoading shouldBe false

                val loadingState = awaitItem()
                loadingState.isLoading shouldBe true

                val errorState = awaitItem()
                errorState.isLoading shouldBe false
                errorState.threads shouldBe emptyList()
                errorState.errorMessageSeq shouldBe 1
            }
        }

    @Test
    fun `スレッド一覧取得がキャンセルされてもエラー状態にはならない`() =
        runTest {
            val viewModel =
                createViewModel(
                    getAllThreads = {
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
                    getAllThreads = {
                        callCount++
                        if (callCount == 1) {
                            throw RuntimeException("test exception")
                        }
                        listOf(Thread("thread-1", "space1", "retry success", "body"))
                    }
                )

            viewModel.uiState.test {
                awaitItem()
                awaitItem()

                val errorState = awaitItem()
                errorState.errorMessageSeq shouldBe 1

                viewModel.onErrorMessageShown()
                val clearedState = awaitItem() // errorMessageSeqがnullに戻っただけの状態
                clearedState.errorMessageSeq shouldBe null

                viewModel.onRetryClick()
                val retryLoadingState = awaitItem()
                retryLoadingState.isLoading shouldBe true

                val successState = awaitItem()
                successState.isLoading shouldBe false
                successState.threads.size shouldBe 1
                successState.threads[0].id shouldBe "thread-1"
                successState.errorMessageSeq shouldBe null
            }
        }
}

private class FakeSpaceRepository(
    private val getAllThreadsImpl: suspend (String) -> List<Thread>,
) : SpaceRepository {
    override suspend fun getAllThreads(spaceId: String): List<Thread> = getAllThreadsImpl(spaceId)

    override suspend fun getMessagesForThread(threadId: String): List<ThreadMessage> = emptyList()
}
