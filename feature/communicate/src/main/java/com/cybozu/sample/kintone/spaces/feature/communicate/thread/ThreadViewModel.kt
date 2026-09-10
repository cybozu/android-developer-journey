package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
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

    private var errorMessageSeq = 0
    private var sendErrorMessageSeq = 0

    init {
        loadMessages()
    }

    private fun loadMessages(refresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value =
                if (refresh) {
                    _uiState.value.copy(isRefreshing = true)
                } else {
                    _uiState.value.copy(isLoading = true)
                }
            try {
                val threadMessages = repository.getMessagesForThread(threadId = threadId)
                _uiState.value =
                    _uiState.value.copy(
                        threadMessages = threadMessages,
                        isLoading = false,
                        isRefreshing = false
                    )
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                errorMessageSeq++
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessageSeq = errorMessageSeq
                    )
            }
        }
    }

    fun onRetryClick() {
        loadMessages()
    }

    fun onRefresh() {
        loadMessages(refresh = true)
    }

    fun onErrorMessageShown() {
        _uiState.value = _uiState.value.copy(errorMessageSeq = null)
    }

    fun onComposeClick() {
        _uiState.value = _uiState.value.copy(isComposerOpen = true)
    }

    fun onCloseComposeClick() {
        _uiState.value = _uiState.value.copy(isComposerOpen = false, inputText = "")
    }

    fun onInputTextChange(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun onSendClick() {
        val text = _uiState.value.inputText
        if (text.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSending = true)
            try {
                repository.postMessage(spaceId = spaceId, threadId = threadId, text = text)
                _uiState.value =
                    _uiState.value.copy(
                        isSending = false,
                        inputText = "",
                        isComposerOpen = false
                    )
                reloadMessagesAfterSend()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                sendErrorMessageSeq++
                _uiState.value =
                    _uiState.value.copy(
                        isSending = false,
                        sendErrorMessageSeq = sendErrorMessageSeq
                    )
            }
        }
    }

    // isLoading/isRefreshingを変更しないため、Pull to Refreshのインジケータは表示されない
    private fun reloadMessagesAfterSend() {
        viewModelScope.launch {
            try {
                val threadMessages = repository.getMessagesForThread(threadId = threadId)
                _uiState.value = _uiState.value.copy(threadMessages = threadMessages)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                errorMessageSeq++
                _uiState.value = _uiState.value.copy(errorMessageSeq = errorMessageSeq)
            }
        }
    }

    fun onSendErrorMessageShown() {
        _uiState.value = _uiState.value.copy(sendErrorMessageSeq = null)
    }
}
