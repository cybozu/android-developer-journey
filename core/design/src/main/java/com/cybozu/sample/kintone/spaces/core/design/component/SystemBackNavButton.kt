package com.cybozu.sample.kintone.spaces.core.design.component

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Androidシステムの戻るアクション(戻るボタン/戻るジェスチャー)と同じ挙動を持つナビゲーションボタン。
 */
@Composable
fun SystemBackNavButton(modifier: Modifier = Modifier) {
    val backPressOwner = LocalOnBackPressedDispatcherOwner.current

    IconButton(
        modifier = modifier,
        onClick = {
            backPressOwner?.onBackPressedDispatcher?.onBackPressed()
        }
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "back button"
        )
    }
}
