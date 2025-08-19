package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@AssistedFactory
interface ThreadViewModelFactory {
    fun create(threadId: String): ThreadViewModel
}

@HiltViewModel(assistedFactory = ThreadViewModelFactory::class)
class ThreadViewModel @AssistedInject constructor(
    @Assisted private val threadId: String,
    private val repository: SpaceRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ThreadUiState())
    val uiState: StateFlow<ThreadUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    fun errorMessageShown() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository
                .getMessagesForThread(threadId = threadId)
                .onSuccess { threadMessages ->
                    _uiState.value =
                        _uiState.value.copy(
                            threadMessages = threadMessages,
                            isLoading = false
                        )
                }.onFailure {
                    // 'it' is the Throwable (error) here
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "メッセージを取得できませんでした\n原因: ${getLocalizedErrorMessage(it)}"
                        )
                }
        }
    }

    private fun getLocalizedErrorMessage(throwable: Throwable): String =
        when (throwable) {
            is java.net.UnknownHostException -> "サーバーが見つかりません"
            is java.net.SocketTimeoutException -> "通信がタイムアウトしました"
            is java.io.IOException -> "ネットワークエラーが発生しました"
            else -> "不明なエラー"
        }
}
