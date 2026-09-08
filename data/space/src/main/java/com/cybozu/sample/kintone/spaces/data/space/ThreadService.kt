package com.cybozu.sample.kintone.spaces.data.space

import com.cybozu.sample.kintone.spaces.data.space.entity.PostThreadCommentBody
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadCommentResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

internal interface ThreadService {
    @POST("k/v1/space/thread/comment.json")
    suspend fun postComment(
        @Header("X-Cybozu-Authorization") encodeString: String,
        @Body body: PostThreadCommentBody,
    ): ThreadCommentResponse
}