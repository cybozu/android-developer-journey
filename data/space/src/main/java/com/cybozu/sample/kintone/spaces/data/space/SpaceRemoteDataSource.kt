package com.cybozu.sample.kintone.spaces.data.space

import com.cybozu.sample.kintone.spaces.data.space.entity.GetAllThreadsBody
import com.cybozu.sample.kintone.spaces.data.space.entity.GetMessagesForThreadBody
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadListResponse
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessageResponse
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

    suspend fun getMessagesForThread(threadId: String): ThreadMessageResponse {
        return spaceService.getMessagesForThread(
            encodeString = usernamePassword.encode().base64(),
            body = GetMessagesForThreadBody(threadId = threadId)
        )
    }
}