package com.cybozu.sample.kintone.spaces.data.space

import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage

interface ThreadRepository {
    suspend fun getMessages(threadId: String): List<ThreadMessage>

    suspend fun sendMessage(
        threadId: String,
        message: String,
    )
}
