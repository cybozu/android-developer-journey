package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
import com.cybozu.sample.kintone.spaces.data.space.entity.BodyComment
import com.cybozu.sample.kintone.spaces.data.space.entity.CommentMessageForThreadBody
import com.cybozu.sample.kintone.spaces.data.space.entity.Creator
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlin.concurrent.thread
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

    fun refreshMessages() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        loadMessages()
    }

    fun postMessage(message: String){
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isPosting = true)
                val id = repository.commentMessageForThread(threadId = threadId, message = message)
                val threadMessagesList = listOf(
                    ThreadMessage(
                        id= id,
                        body = message,
                        creator = Creator(name = "久米 弘汰"),
                        comments = emptyList()
                    )
                )

                _uiState.value= _uiState.value.copy(
                    // List型で合わせる
                    threadMessages = threadMessagesList + _uiState.value.threadMessages,
                )
            }catch (e: CancellationException){
                throw e
            }catch (_ : Exception){
                _uiState.value = _uiState.value.copy(isPosting = false)
                _uiState.value = _uiState.value.copy(isPostError = true)
            }
        }
    }

    private fun loadMessages() {
        // launchの中...Coroutine: 非同期処理　Exceptionの形式には注意
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val threadMessages = repository.getMessagesForThread(threadId = threadId)
                _uiState.value =
                    _uiState.value.copy(
                        threadMessages = threadMessages,
                        isLoading = false,
                        isError = false, // isError: falseに設定
                        isRefreshing = false
                    )
            } catch (e: CancellationException) {
                throw e // Catch & Release CancellationException
            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        threadMessages = emptyList(),
                        isLoading = false,
                        isError = true, // 失敗した場合、isError: trueに設定
                        isRefreshing = false
                    )
            }
        }
    }
}
