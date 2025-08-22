package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
import com.cybozu.sample.kintone.spaces.data.space.entity.PostComment
import com.cybozu.sample.kintone.spaces.data.space.entity.PostMessage
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

    private fun initialMessages() {
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

    fun openDialog() {
        (_uiState.value as? ThreadUiState.Success)?.let {
            _uiState.value =
                it.copy(
                    isDialogVisible = true
                )
        }
    }

    fun closeDialog() {
        (_uiState.value as? ThreadUiState.Success)?.let {
            _uiState.value =
                it.copy(
                    isDialogVisible = false,
                    inputText = "",
                    isPostError = false,
                    postErrorMessageId = null
                )
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

    fun onTextChanged(newText: String) {
        (_uiState.value as? ThreadUiState.Success)?.let {
            _uiState.value =
                it.copy(
                    inputText = newText
                )
        }
    }

    fun sendComment() {
        (_uiState.value as? ThreadUiState.Success)?.let { successUiState ->
            if (successUiState.inputText.isBlank()) {
                _uiState.value =
                    successUiState.copy(
                        isPostError = true,
                        postErrorMessageId = R.string.post_blank
                    )
                return
            }
            viewModelScope.launch {
                val result =
                    repository.postMessageForThread(
                        postMessage =
                            PostMessage(
                                space = "3",
                                thread = threadId,
                                comment =
                                    PostComment(
                                        text = successUiState.inputText
                                    )
                            )
                    )
                result
                    .onSuccess {
                        loadMessages()
                    }.onFailure {
                        _uiState.value =
                            successUiState.copy(
                                isPostError = true,
                                postErrorMessageId = R.string.post_io_exception
                            )
                    }
            }
        }
    }
}
