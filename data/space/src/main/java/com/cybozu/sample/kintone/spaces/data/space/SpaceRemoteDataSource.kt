package com.cybozu.sample.kintone.spaces.data.space

import javax.inject.Inject
import okio.ByteString.Companion.encode

class SpaceRemoteDataSource @Inject constructor(
    private val spaceService: SpaceService,
) {
    private val usernamePassword = "${BuildConfig.USER}:${BuildConfig.PASSWORD}"

    suspend fun getAllThreads(spaceId: String): List<KintoneThread> {
        return spaceService.getAllThreads(
            encodeString = usernamePassword.encode().base64(),
            body = GetAllThreadsBody(spaceId = spaceId)
        )
    }

    suspend fun getMessagesForThread(threadId: String): List<KintoneMessage> {
        return spaceService.getMessagesForThread(
            encodeString = usernamePassword.encode().base64(),
            body = GetMessagesForThreadBody(threadId = threadId)
        )
    }
}