package com.cybozu.sample.kintone.spaces.data.space

import com.cybozu.sample.kintone.spaces.data.login.CredentialRepository
import com.cybozu.sample.kintone.spaces.data.space.entity.GetAllThreadsBody
import com.cybozu.sample.kintone.spaces.data.space.entity.GetMessagesForThreadBody
import com.cybozu.sample.kintone.spaces.data.space.entity.PostMessageBody
import com.cybozu.sample.kintone.spaces.data.space.entity.PostMessageResponse
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadListResponse
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessageResponse
import javax.inject.Inject
import okio.ByteString.Companion.encode
import retrofit2.Retrofit

internal class SpaceRemoteDataSource @Inject constructor(
    retrofit: Retrofit,
    private val credentialRepository: CredentialRepository,
) {
    private val spaceService: SpaceService = retrofit.create(SpaceService::class.java)

    private suspend fun buildAuthHeader(): String {
        val credential =
            requireNotNull(credentialRepository.getCredential()) {
                "ログインしていない状態でAPIが呼ばれました"
            }
        return "${credential.userName}:${credential.password}".encode().base64()
    }

    suspend fun getAllThreads(spaceId: String): ThreadListResponse =
        spaceService.getAllThreads(
            encodeString = buildAuthHeader(),
            body = GetAllThreadsBody(spaceId = spaceId)
        )

    suspend fun getMessagesForThread(threadId: String): ThreadMessageResponse =
        spaceService.getMessagesForThread(
            encodeString = buildAuthHeader(),
            body = GetMessagesForThreadBody(threadId = threadId)
        )

    suspend fun postMessage(
        spaceId: String,
        threadId: String,
        body: String,
    ): PostMessageResponse =
        spaceService.postMessage(
            encodeString = buildAuthHeader(),
            body =
                PostMessageBody(
                    space = spaceId,
                    thread = threadId,
                    comment = PostMessageBody.CommentBody(text = body)
                )
        )
}
