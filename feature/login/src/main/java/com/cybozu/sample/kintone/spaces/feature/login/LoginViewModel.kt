package com.cybozu.sample.kintone.spaces.feature.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class LoginViewModel @Inject constructor(
    // 多分リポジトリを作る
): ViewModel(){
    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onInputUserName(text: String) {
        _uiState.value = _uiState.value.copy(userName = text)
    }

    fun onInputPassword(text: String) {
        _uiState.value = _uiState.value.copy(password = text)
    }

    fun onLoginClick(){
        // TODO ログインの処理を実装
    }
}