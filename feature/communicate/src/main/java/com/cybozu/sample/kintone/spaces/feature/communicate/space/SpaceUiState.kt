package com.cybozu.sample.kintone.spaces.feature.communicate.space

import com.cybozu.sample.kintone.spaces.data.space.entity.Thread

data class SpaceUiState(
    val threads: List<Thread> = emptyList(),
    val isLoading: Boolean = false,
)
