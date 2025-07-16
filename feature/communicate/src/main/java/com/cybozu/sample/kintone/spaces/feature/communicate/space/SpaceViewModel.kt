package com.cybozu.sample.kintone.spaces.feature.communicate.space

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybozu.sample.kintone.spaces.data.space.KintoneThread
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SpaceViewModel @Inject constructor(
    private val repository: SpaceRepository
) : ViewModel() {

    private val _threads = MutableStateFlow<List<KintoneThread>>(emptyList())
    val threads: StateFlow<List<KintoneThread>> = _threads.asStateFlow()

    init {
        loadThreads()
    }

    private fun loadThreads() {
        viewModelScope.launch {
            _threads.value = repository.getAllThreads()
        }
    }
}
