package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage

data class ThreadUiState(
    val threadMessages: List<ThreadMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null, // エラーメッセージを文字列で直接管理する。
    // Booleanで管理することも最初考慮したが、エラーにも「サーバーエラー」や「ネットワークエラー」
    // など様々なエラーが発生しうると考え、Stringでの管理を採用した。
)
