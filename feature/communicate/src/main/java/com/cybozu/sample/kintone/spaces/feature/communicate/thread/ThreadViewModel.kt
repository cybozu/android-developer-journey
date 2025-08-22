package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybozu.sample.kintone.spaces.data.space.ThreadRepository
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
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
    private val repository: ThreadRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ThreadUiState>(ThreadUiState.Initial)
    val uiState: StateFlow<ThreadUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    fun refreshMessages() {
        loadMessages()
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            repository.sendMessage(threadId, message)
            loadMessages()
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _uiState.value = ThreadUiState.Loading
            try {
                val threadMessages: List<ThreadMessage> = repository.getMessages(threadId)
                _uiState.value = ThreadUiState.Success(threadMessages)
            } catch (e: IOException) {
                _uiState.value = ThreadUiState.Error("メッセージが取得できませんでした")
            }
        }
    }
}
