package com.cybozu.sample.kintone.spaces

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.cybozu.sample.kintone.spaces.core.design.theme.KintoneSpacesTheme
import com.cybozu.sample.kintone.spaces.feature.communicate.SpaceRoute
import com.cybozu.sample.kintone.spaces.feature.communicate.ThreadRoute
import com.cybozu.sample.kintone.spaces.feature.communicate.communicateNavigation
import com.cybozu.sample.kintone.spaces.feature.login.LoginRoute
import com.cybozu.sample.kintone.spaces.feature.login.loginNavigation
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

@Composable
fun KintoneSpacesApp() {
    val backStack = rememberNavBackStack(LoginRoute)
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        // ViewModelStoreNavEntryDecorator を追加するため、デフォルトの
        // NavEntryDecorator も明示的に指定する必要がある
        entryDecorators =
            listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
        entryProvider =
            entryProvider {
                communicateNavigation(
                    onThreadClick = { threadId, threadName ->
                        backStack.add(ThreadRoute(threadId = threadId, threadName = threadName))
                    }
                )
                loginNavigation(
                    onLoginSuccess = {
                        backStack.add(SpaceRoute)
                    }
                )
            }
    )
}
