package com.cybozu.sample.kintone.spaces.feature.communicate.space

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybozu.sample.kintone.spaces.data.space.SpaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SpaceViewModel
    @Inject
    constructor(
        private val repository: SpaceRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(SpaceUiState())
        val uiState: StateFlow<SpaceUiState> = _uiState.asStateFlow()

        init {
            loadThreads()
        }

        private fun loadThreads() {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                try {
                    val threads = repository.getAllThreads(spaceId = "3")
                    _uiState.value =
                        _uiState.value.copy(
                            threads = threads,
                            isLoading = false
                        )
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }
