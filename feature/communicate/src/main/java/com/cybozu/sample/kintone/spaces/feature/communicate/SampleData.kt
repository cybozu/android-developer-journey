package com.cybozu.sample.kintone.spaces.feature.communicate

val sampleThreads = List(10) { ThreadItemData(id = "thread-$it", title = "Thread Title $it", lastMessageSnippet = "This is a snippet of the last message in thread $it...") }

val sampleMessages = List(50) {
    MessageItemData(
        id = "msg-$it",
        threadId = "thread-${it % 10}",
        userName = "User ${it % 5}",
        userIconUrl = "",
        content = "This is message number $it in thread ${it % 10}. Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    )
}
