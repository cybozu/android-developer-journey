package com.cybozu.sample.kintone.spaces.data.space

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SpaceService {
    @POST("k/api/space/thread/list.json")
    suspend fun getAllThreads(
        @Header("X-Cybozu-Authorization") encodeString: String,
        @Body body : GetAllThreadsBody
    ): List<KintoneThread>

    @POST("k/api/space/thread/post/list.json")
    suspend fun getMessagesForThread(
        @Header("X-Cybozu-Authorization") encodeString: String,
        @Body body : GetMessagesForThreadBody
    ): List<KintoneMessage>
}