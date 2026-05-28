package com.pistolshooting.presentation.screens.career

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pistolshooting.data.local.entity.PlayerProgressEntity
import com.pistolshooting.domain.model.CareerRank
import com.pistolshooting.domain.model.LevelCatalog
import com.pistolshooting.domain.model.LevelConfig
import com.pistolshooting.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class CareerUiState(
    val progress: PlayerProgressEntity? = null,
    val availableLevels: List<LevelConfig> = emptyList(),
    val currentRank: CareerRank = CareerRank.BEGINNER,
    val isLoading: Boolean = true
)

@HiltViewModel
class CareerViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    val uiState: StateFlow<CareerUiState> = repository.observePlayerProgress()
        .filterNotNull()
        .map { progress ->
            val levels = LevelCatalog.allLevels.filter { it.unlockLevel <= progress.playerLevel }
            val rank = CareerRank.entries
                .filter { it.requiredLevel <= progress.playerLevel }
                .maxByOrNull { it.requiredLevel } ?: CareerRank.BEGINNER
            CareerUiState(
                progress = progress,
                availableLevels = levels,
                currentRank = rank,
                isLoading = false
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CareerUiState())
}
