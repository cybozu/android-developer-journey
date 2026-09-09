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

    private fun createViewModel(shouldFail: Boolean): ThreadViewModel {
        val repository = FakeSpaceRepository(shouldFail = shouldFail)
        return ThreadViewModel(threadId = "thread-1", repository = repository)
    }

    @Test
    fun `メッセージ一覧が取得できる`() =
        runTest {
            val viewModel = createViewModel(shouldFail = false)

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
                loadedState.errorMessage shouldBe null
            }
        }

    @Test
    fun `メッセージ取得に失敗したときエラー状態になる`() =
        runTest {
            val viewModel = createViewModel(shouldFail = true)

            viewModel.uiState.test {
                val initialState = awaitItem()
                initialState.threadMessages shouldBe emptyList()
                initialState.isLoading shouldBe false

                val loadingState = awaitItem()
                loadingState.threadMessages shouldBe emptyList()
                loadingState.isLoading shouldBe true

                val errorState = awaitItem()
                errorState.threadMessages shouldBe emptyList()
                errorState.errorMessage shouldNotBe null
                errorState.isLoading shouldBe false
            }
        }

    @Test
    fun `clearErrorMessageでerrorMessageがnullに戻る`() =
        runTest {
            val viewModel = createViewModel(shouldFail = true)

            viewModel.uiState.test {
                awaitItem() // initialState

                awaitItem() // loadingState

                val errorState = awaitItem()
                errorState.errorMessage shouldNotBe null

                viewModel.clearErrorMessage()
                val clearedState = awaitItem()
                clearedState.errorMessage shouldBe null
            }
        }
}

private class FakeSpaceRepository(
    private val shouldFail: Boolean,
) : SpaceRepository {
    override suspend fun getAllThreads(spaceId: String): List<Thread> = emptyList()

    override suspend fun getMessagesForThread(threadId: String): List<ThreadMessage> {
        if (shouldFail) {
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
}
