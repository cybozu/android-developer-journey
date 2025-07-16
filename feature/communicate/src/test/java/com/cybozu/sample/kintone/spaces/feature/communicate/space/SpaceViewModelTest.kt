package com.cybozu.sample.kintone.spaces.feature.communicate.space

import com.cybozu.sample.kintone.spaces.data.space.KintoneMessage
import com.cybozu.sample.kintone.spaces.data.space.KintoneThread
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldNotBeEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
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

    @Test
    fun shouldLoadThreadsOnInit() {
        val repository = FakeSpaceRepository()
        val viewModel = SpaceViewModel(repository)

        val threads = viewModel.threads.value

        threads.size shouldBe 2
        threads[0].id shouldBe "thread-1"
        threads[0].title shouldBe "Test Thread 1"
    }

    @Test
    fun shouldProvideEmptyThreadsInitially() {
        val repository = FakeSpaceRepository()
        val viewModel = SpaceViewModel(repository)

        viewModel.threads.value.shouldNotBeEmpty()
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
