package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybozu.sample.kintone.spaces.data.space.KintoneMessage
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ThreadViewModel @Inject constructor(
    private val repository: SpaceRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<KintoneMessage>>(emptyList())
    val messages: StateFlow<List<KintoneMessage>> = _messages.asStateFlow()

    fun loadMessages(threadId: String) {
        viewModelScope.launch {
            _messages.value = repository.getMessagesForThread(threadId)
        }
    }
}
