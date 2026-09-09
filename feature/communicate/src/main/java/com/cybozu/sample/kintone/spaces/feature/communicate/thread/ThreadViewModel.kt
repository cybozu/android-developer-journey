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

    private fun fetchMessages(applyInProgress: (state: ThreadUiState, progress: Boolean) -> ThreadUiState) {
        viewModelScope.launch {
            _uiState.value = applyInProgress(_uiState.value.copy(errorMessage = null), true)
            try {
                val threadMessages = repository.getMessagesForThread(threadId = threadId)
                _uiState.value =
                    applyInProgress(_uiState.value.copy(threadMessages = threadMessages), false)
            } catch (_: Exception) {
                _uiState.value =
                    applyInProgress(
                        _uiState.value.copy(errorMessage = "メッセージを取得できませんでした"),
                        false
                    )
            }
        }
    }

    private fun loadMessages() {
        fetchMessages { state, inProgress -> state.copy(isLoading = inProgress) }
    }

    fun onRefresh() {
        fetchMessages { state, inProgress -> state.copy(isRefreshing = inProgress) }
    }

    fun onErrorDialogDismissed() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
