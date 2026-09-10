package com.cybozu.sample.kintone.spaces.feature.communicate

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.cybozu.sample.kintone.spaces.feature.communicate.space.SpaceScreen
import com.cybozu.sample.kintone.spaces.feature.communicate.thread.ThreadScreen

fun EntryProviderScope<NavKey>.communicateNavigation(onThreadClick: (threadId: String, threadName: String) -> Unit) {
    entry<SpaceRoute> {
        SpaceScreen(
            onThreadClick = { thread ->
                onThreadClick(thread.id, thread.name)
            }
        )
    }

    entry<ThreadRoute> { threadRoute ->
        ThreadScreen(
            threadId = threadRoute.threadId,
            threadName = threadRoute.threadName
        )
    }
}
