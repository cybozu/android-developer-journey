package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage

sealed interface ThreadUiState {
    object Loading : ThreadUiState
    object Initial : ThreadUiState

    data class Success(
        val threadMessage: List<ThreadMessage>,
    ) : ThreadUiState

    data class Error(
        val errorMessage: String,
    ) : ThreadUiState
}
