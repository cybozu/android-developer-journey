package com.cybozu.sample.kintone.spaces.feature.communicate.space

import app.cash.turbine.test
import com.cybozu.sample.kintone.spaces.data.space.KintoneMessage
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
import com.cybozu.sample.kintone.spaces.data.space.Thread
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
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

    private fun createViewModel(): SpaceViewModel {
        val repository = FakeSpaceRepository()
        return SpaceViewModel(repository)
    }

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
                loadedState.isLoading shouldBe false
            }
        }
}

private class FakeSpaceRepository : SpaceRepository {
    override suspend fun getAllThreads(spaceId: String): List<Thread> {
        delay(100) // 通信時間を模擬
        return listOf(
            Thread("thread-1", "space1","Test Thread 1", "Last message 1"),
            Thread("thread-2", "space2","Test Thread 2", "Last message 2")
        )
    }

    override suspend fun getMessagesForThread(threadId: String): List<KintoneMessage> = emptyList()
}
