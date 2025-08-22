package com.cybozu.sample.kintone.spaces.data.space

import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage
import javax.inject.Inject

internal class ThreadRepositoryImpl @Inject constructor(
    private val spaceRemoteDataSource: SpaceRemoteDataSource,
) : ThreadRepository {
    override suspend fun getMessages(threadId: String): List<ThreadMessage> =
        spaceRemoteDataSource.getMessagesForThread(threadId).result.items

    override suspend fun sendMessage(
        threadId: String,
        message: String,
    ) {
        spaceRemoteDataSource.sendMessage(threadId, message)
    }
}
