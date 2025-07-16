package com.cybozu.sample.kintone.spaces

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.cybozu.sample.kintone.spaces.core.design.theme.KintoneSpacesTheme
import com.cybozu.sample.kintone.spaces.feature.communicate.SpaceRoute
import com.cybozu.sample.kintone.spaces.feature.communicate.communicateNavigation
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
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = SpaceRoute
    ) {
        communicateNavigation(navController)
    }
}
