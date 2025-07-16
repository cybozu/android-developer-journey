package com.cybozu.sample.kintone.spaces.feature.communicate.space

import app.cash.turbine.test
import com.cybozu.sample.kintone.spaces.data.space.KintoneMessage
import com.cybozu.sample.kintone.spaces.data.space.KintoneThread
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldNotBeEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SpaceViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
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
    fun `スレッド一覧が取得できる`() = runTest {
        val viewModel = createViewModel()

        viewModel.threads.test {
            val threads = expectMostRecentItem()

            threads.size shouldBe 2
            threads[0].id shouldBe "thread-1"
            threads[0].title shouldBe "Test Thread 1"
        }
    }
}

private class FakeSpaceRepository : SpaceRepository {
    override fun getAllThreads(): List<KintoneThread> {
        return listOf(
            KintoneThread("thread-1", "Test Thread 1", "Last message 1"),
            KintoneThread("thread-2", "Test Thread 2", "Last message 2")
        )
    }

    override fun getMessagesForThread(threadId: String): List<KintoneMessage> {
        return emptyList()
    }
}
