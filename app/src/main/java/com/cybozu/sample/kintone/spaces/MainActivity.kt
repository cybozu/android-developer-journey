package com.cybozu.sample.kintone.spaces

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cybozu.sample.kintone.spaces.core.design.theme.KintoneSpacesTheme
import com.cybozu.sample.kintone.spaces.feature.communicate.thread.ThreadScreen
import com.cybozu.sample.kintone.spaces.feature.communicate.sampleMessages
import com.cybozu.sample.kintone.spaces.feature.communicate.sampleThreads
import com.cybozu.sample.kintone.spaces.feature.communicate.space.SpaceScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KintoneSpacesTheme {
                KintoneSpacesApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KintoneSpacesApp() {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Kintone Spaces") })
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "space",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("space") {
                SpaceScreen(navController = navController, threads = sampleThreads)
            }
            composable(
                route = "thread/{threadId}",
                arguments = listOf(navArgument("threadId") { type = NavType.StringType })
            ) { backStackEntry ->
                val threadId = backStackEntry.arguments?.getString("threadId")
                ThreadScreen(
                    navController = navController,
                    threadId = threadId,
                    messages = sampleMessages.filter { it.threadId == threadId }
                )
            }
        }
    }
}
