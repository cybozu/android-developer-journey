package com.cybozu.sample.kintone.spaces.data.space

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalSpaceRepository @Inject constructor() : SpaceRepository {

    override fun getAllThreads(): List<KintoneThread> {
        return List(10) {
            KintoneThread(
                id = "thread-$it",
                title = "Thread Title $it",
                lastMessageSnippet = "This is a snippet of the last message in thread $it..."
            )
        }
    }

    override fun getMessagesForThread(threadId: String): List<KintoneMessage> {
        return List(50) {
            KintoneMessage(
                id = "msg-$it",
                threadId = "thread-${it % 10}",
                userName = "User ${it % 5}",
                userIconUrl = "",
                content = "This is message number $it in thread ${it % 10}. Lorem ipsum dolor sit amet, consectetur adipiscing elit."
            )
        }.filter { it.threadId == threadId }
    }
}
