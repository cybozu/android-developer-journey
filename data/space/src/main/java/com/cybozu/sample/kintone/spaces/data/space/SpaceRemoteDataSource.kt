package com.cybozu.sample.kintone.spaces.data.space

import javax.inject.Inject
import okio.ByteString.Companion.encode
import retrofit2.Retrofit

class SpaceRemoteDataSource @Inject constructor(
    retrofit: Retrofit
) {
    private val spaceService: SpaceService = retrofit.create(SpaceService::class.java)
    private val usernamePassword = "${BuildConfig.USER}:${BuildConfig.PASSWORD}"

    suspend fun getAllThreads(spaceId: String): ThreadListResponse {
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