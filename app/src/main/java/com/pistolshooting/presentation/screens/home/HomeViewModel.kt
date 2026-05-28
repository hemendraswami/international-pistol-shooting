package com.pistolshooting.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pistolshooting.data.local.entity.PlayerProgressEntity
import com.pistolshooting.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val playerProgress: PlayerProgressEntity? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = repository.observePlayerProgress()
        .map { progress ->
            if (progress == null) {
                // First run — initialize default progress
                val defaultProgress = PlayerProgressEntity()
                repository.savePlayerProgress(defaultProgress)
                HomeUiState(playerProgress = defaultProgress, isLoading = false)
            } else {
                HomeUiState(playerProgress = progress, isLoading = false)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}
