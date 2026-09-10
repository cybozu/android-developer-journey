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

    fun refresh() {
        loadMessages(isRefresh = true)
    }

    fun updateInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun postComment() {
        val text = _uiState.value.inputText
        if (text.isBlank() || _uiState.value.isPosting) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isPosting = true, hasPostError = false)
            try {
                repository.postThreadComment(
                    spaceId = SPACE_ID,
                    threadId = threadId,
                    text = text
                )
                _uiState.value = _uiState.value.copy(inputText = "", isPosting = false)
                loadMessages(isRefresh = true)
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(isPosting = false, hasPostError = true)
            }
        }
    }

    private fun loadMessages(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    hasError = false
                )
            try {
                val threadMessages = repository.getMessagesForThread(threadId = threadId)
                _uiState.value =
                    _uiState.value.copy(
                        threadMessages = threadMessages,
                        isLoading = false,
                        isRefreshing = false
                    )
            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        hasError = true
                    )
            }
        }
    }

    companion object {
        private const val SPACE_ID = "3"
    }
}
