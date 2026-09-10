package com.cybozu.sample.kintone.spaces.feature.login

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

fun EntryProviderScope<NavKey>.loginNavigation(onLoginSuccess: () -> Unit) {
    entry<LoginRoute> {
        LoginScreen(
            onLoginSuccess = onLoginSuccess
        )
    }
}
