package com.cybozu.sample.kintone.spaces.feature.communicate

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.cybozu.sample.kintone.spaces.feature.communicate.space.SpaceScreen
import com.cybozu.sample.kintone.spaces.feature.communicate.thread.ThreadScreen

fun EntryProviderScope<NavKey>.communicateNavigation(backStack: MutableList<NavKey>) {
    entry<SpaceRoute> {
        SpaceScreen(
            onThreadClick = { thread ->
                backStack.add(ThreadRoute(threadId = thread.id, threadName = thread.name))
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
