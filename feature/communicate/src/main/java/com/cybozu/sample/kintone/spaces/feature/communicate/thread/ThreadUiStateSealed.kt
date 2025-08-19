package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage

sealed interface ThreadUiStateSealed {
    object Loading : ThreadUiStateSealed

    data class Success(
        val threadMessage: List<ThreadMessage>,
    ) : ThreadUiStateSealed

    data class Error(
        val errorMessage: String,
    ) : ThreadUiStateSealed
}
