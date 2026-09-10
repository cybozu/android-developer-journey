package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage

data class ThreadUiState(
    val threadMessages: List<ThreadMessage> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val isRefreshing: Boolean = false,
    val inputText: String = "",
    val isPosting: Boolean = false,
    val hasPostError: Boolean = false,
)
