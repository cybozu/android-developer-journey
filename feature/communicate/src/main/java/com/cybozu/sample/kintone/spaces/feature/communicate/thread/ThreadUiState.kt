package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage

data class ThreadUiState(
    val threadMessages: List<ThreadMessage> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false,
    val inputText: String = "",
)
