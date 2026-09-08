package com.cybozu.sample.kintone.spaces.data.space

import com.cybozu.sample.kintone.spaces.data.space.entity.PostThreadCommentBody
import javax.inject.Inject
import okio.ByteString.Companion.encode
import retrofit2.Retrofit

internal class ThreadRemoteDataSource @Inject constructor(
    retrofit: Retrofit,
)  {
    private val service = retrofit.create(ThreadService::class.java)
    private val usernamePassword = "${BuildConfig.USER}:${BuildConfig.PASSWORD}"

    suspend fun postComment(
        spaceId: Int,
        threadId: Int,
        comment: String
    ) = service.postComment(
        encodeString = usernamePassword.encode().base64(),
        body = PostThreadCommentBody(
            space = spaceId,
            thread = threadId,
            comment = PostThreadCommentBody.Comment(
                comment
            )
        )
    )
}