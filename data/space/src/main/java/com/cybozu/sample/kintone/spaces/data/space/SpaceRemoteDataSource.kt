package com.cybozu.sample.kintone.spaces.data.space

import com.cybozu.sample.kintone.spaces.data.space.entity.GetAllThreadsBody
import com.cybozu.sample.kintone.spaces.data.space.entity.GetMessagesForThreadBody
import com.cybozu.sample.kintone.spaces.data.space.entity.PostMessage
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadListResponse
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessageResponse
import javax.inject.Inject
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import okio.ByteString.Companion.encode
import okio.IOException
import retrofit2.Retrofit

internal class SpaceRemoteDataSource @Inject constructor(
    retrofit: Retrofit,
) {
    private val spaceService: SpaceService = retrofit.create(SpaceService::class.java)
    private val usernamePassword = "${BuildConfig.USER}:${BuildConfig.PASSWORD}"

    suspend fun getAllThreads(spaceId: String): ThreadListResponse =
        spaceService.getAllThreads(
            encodeString = usernamePassword.encode().base64(),
            body = GetAllThreadsBody(spaceId = spaceId)
        )

    suspend fun getMessagesForThread(threadId: String): Result<ThreadMessageResponse> =
        try {
            success(
                spaceService.getMessagesForThread(
                    encodeString = usernamePassword.encode().base64(),
                    body = GetMessagesForThreadBody(threadId = threadId)
                )
            )
        } catch (e: IOException) {
            failure(e)
        }

    suspend fun postMessageForThread(postMessage: PostMessage): Result<Unit> =
        try {
            spaceService.postMessageForThread(
                encodeString = usernamePassword.encode().base64(),
                body = postMessage
            )
            success(Unit)
        } catch (e: IOException) {
            failure(e)
        }
}
