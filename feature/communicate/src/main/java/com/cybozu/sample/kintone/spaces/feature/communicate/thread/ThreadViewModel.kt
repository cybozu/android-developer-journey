package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
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
    private val repository: SpaceRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ThreadUiState>(ThreadUiState.Loading)
    val uiState: StateFlow<ThreadUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _uiState.value = ThreadUiState.Loading
            try {
                val threadMessages = repository.getMessagesForThread(threadId = threadId)
                _uiState.value = ThreadUiState.Success(threadMessages)
            } catch (e: IOException) {
                _uiState.value = ThreadUiState.Error("メッセージが取得できませんでした")
            }
        }
    }
}
