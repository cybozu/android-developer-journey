package com.cybozu.sample.kintone.spaces.data.space

interface SpaceRepository {
    fun getAllThreads(): List<KintoneThread>
    fun getMessagesForThread(threadId: String): List<KintoneMessage>
}
