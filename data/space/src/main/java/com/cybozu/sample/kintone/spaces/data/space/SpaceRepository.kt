package com.cybozu.sample.kintone.spaces.data.space

import com.cybozu.sample.kintone.spaces.data.space.entity.Thread

interface SpaceRepository {
    suspend fun getAllThreads(spaceId: String): List<Thread>

    suspend fun getMessagesForThread(threadId: String): List<KintoneMessage>
}
