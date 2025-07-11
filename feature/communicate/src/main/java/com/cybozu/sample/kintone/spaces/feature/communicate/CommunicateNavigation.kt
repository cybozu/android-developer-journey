package com.cybozu.sample.kintone.spaces.feature.communicate

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cybozu.sample.kintone.spaces.feature.communicate.space.SpaceScreen
import com.cybozu.sample.kintone.spaces.feature.communicate.thread.ThreadScreen

fun NavGraphBuilder.communicateNavigation(navController: NavController) {
    composable<SpaceRoute> {
        SpaceScreen(
            threads = sampleThreads,
            onThreadClick = { threadId ->
                navController.navigate(ThreadRoute(threadId = threadId))
            }
        )
    }

    composable<ThreadRoute> { backStackEntry ->
        val threadRoute = backStackEntry.toRoute<ThreadRoute>()
        ThreadScreen(
            threadId = threadRoute.threadId,
            messages = sampleMessages.filter { it.threadId == threadRoute.threadId }
        )
    }
}
