package com.cybozu.sample.kintone.spaces.feature.login

data class LoginUiState(
    val userName: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
)
