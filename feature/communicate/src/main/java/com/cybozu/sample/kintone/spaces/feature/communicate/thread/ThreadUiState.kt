package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import androidx.annotation.StringRes
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage

sealed class ThreadUiState {
    object Idle : ThreadUiState()

    object Loading : ThreadUiState()

    data class Error(
        @param:StringRes val messageId: Int,
    ) : ThreadUiState()

    data class Success(
        val threadMessages: List<ThreadMessage>,
        val isInputVisible: Boolean = false,
        val inputText: String = "",
    ) : ThreadUiState()

    data class Refreshing(
        val threadMessages: List<ThreadMessage> = emptyList(),
    ) : ThreadUiState()
}
