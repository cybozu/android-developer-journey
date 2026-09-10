package com.cybozu.sample.kintone.spaces.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybozu.sample.kintone.spaces.data.login.CredentialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val credentialRepository: CredentialRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onInputUserName(text: String) {
        _uiState.value = _uiState.value.copy(userName = text)
    }

    fun onInputPassword(text: String) {
        _uiState.value = _uiState.value.copy(password = text)
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            credentialRepository.saveCredential(
                userName = _uiState.value.userName,
                password = _uiState.value.password
            )

            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    isLoginSuccess = true
                )
        }
    }
}
