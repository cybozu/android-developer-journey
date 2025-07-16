package com.cybozu.sample.kintone.spaces.feature.communicate.space

import com.cybozu.sample.kintone.spaces.data.space.KintoneThread

data class SpaceUiState(
    val threads: List<KintoneThread> = emptyList(),
    val isLoading: Boolean = false,
)
