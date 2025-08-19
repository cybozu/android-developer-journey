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
        loadMessages() // メッセージのロード
    }

    private fun loadMessages() {
        viewModelScope.launch { // launchの中がコルーチン: 非同期処理　Exceptionの形式には注意
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val threadMessages = repository.getMessagesForThread(threadId = threadId)
                _uiState.value =
                    _uiState.value.copy(
                        threadMessages = threadMessages,
                        isLoading = false,
                        isError = false // isError: falseに設定
                    )
            } catch (e: CancellationException){
                throw e // Catch & Release CancellationException

            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        threadMessages = emptyList(),
                        isLoading = false,
                        isError = true // 失敗した場合、isError: trueに設定
                    )
            }
        }
    }
}
