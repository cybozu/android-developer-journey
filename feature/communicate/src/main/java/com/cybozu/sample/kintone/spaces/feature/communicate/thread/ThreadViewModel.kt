package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@AssistedFactory
interface ThreadViewModelFactory {
    fun create(
        @Assisted("spaceId") spaceId: String,
        @Assisted("threadId") threadId: String,
    ): ThreadViewModel
}

@HiltViewModel(assistedFactory = ThreadViewModelFactory::class)
class ThreadViewModel @AssistedInject constructor(
    @Assisted("spaceId") private val spaceId: String,
    @Assisted("threadId") private val threadId: String,
    private val repository: SpaceRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ThreadUiState())
    val uiState: StateFlow<ThreadUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    fun refresh() {
        refreshMessages()
    }

    fun postMessage(text: String) {
        addMessage(text)
    }

    fun updateText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val threadMessages = repository.getMessagesForThread(threadId = threadId)
                _uiState.value =
                    _uiState.value.copy(
                        threadMessages = threadMessages,
                        isLoading = false,
                        errorMessage = null
                    )
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "メッセージを取得できませんでした"
                    )
            }
        }
    }

    private fun refreshMessages() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            try {
                val threadMessages = repository.getMessagesForThread(threadId = threadId)
                _uiState.value =
                    _uiState.value.copy(
                        threadMessages = threadMessages,
                        isRefreshing = false,
                        errorMessage = null
                    )
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isRefreshing = false,
                        errorMessage = "メッセージを取得できませんでした"
                    )
            }
        }
    }

    private fun addMessage(text: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isPosting = true)
            try {
                repository.addMessageForThread(spaceId = spaceId, threadId = threadId, text = text)
                _uiState.value =
                    _uiState.value.copy(
                        isPosting = false,
                        errorMessage = null,
                        inputText = ""
                    )
                refreshMessages()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isPosting = false,
                        errorMessage = "メッセージを投稿できませんでした"
                    )
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.value =
            _uiState.value.copy(errorMessage = null)
    }
}
