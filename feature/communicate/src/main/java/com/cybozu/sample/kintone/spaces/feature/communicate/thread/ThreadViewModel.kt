package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
import com.cybozu.sample.kintone.spaces.feature.communicate.R
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
    private val _uiState = MutableStateFlow<ThreadUiState>(ThreadUiState.Idle)
    val uiState: StateFlow<ThreadUiState> = _uiState.asStateFlow()

    init {
        initialMessages()
    }

    fun initialMessages() {
        viewModelScope.launch {
            _uiState.value = ThreadUiState.Loading
            loadMessages()
        }
    }

    fun refreshMessages() {
        viewModelScope.launch {
            val threadMessages = (_uiState.value as? ThreadUiState.Success)?.threadMessages ?: emptyList()
            _uiState.value =
                ThreadUiState.Refreshing(
                    threadMessages = threadMessages
                )
            loadMessages()
        }
    }

    private suspend fun loadMessages() {
        val result = repository.getMessagesForThread(threadId = threadId)
        result
            .onSuccess {
                _uiState.value =
                    ThreadUiState.Success(
                        threadMessages = it
                    )
            }.onFailure {
                _uiState.value =
                    ThreadUiState.Error(
                        messageId = R.string.thread_error
                    )
            }
    }
}
