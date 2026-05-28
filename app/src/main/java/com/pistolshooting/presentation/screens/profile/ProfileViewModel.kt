package com.pistolshooting.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pistolshooting.data.local.entity.GameSessionEntity
import com.pistolshooting.data.local.entity.PlayerProgressEntity
import com.pistolshooting.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val progress: PlayerProgressEntity? = null,
    val recentSessions: List<GameSessionEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        repository.observePlayerProgress(),
        repository.observeRecentSessions()
    ) { progress, sessions ->
        ProfileUiState(progress = progress, recentSessions = sessions, isLoading = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    fun upgradeSkill(skill: String) {
        viewModelScope.launch {
            val progress = repository.getDefaultPlayerProgress()
            val cost = 200
            if (progress.coins < cost) return@launch

            val updated = when (skill) {
                "stability" -> progress.copy(stability = (progress.stability + 0.05f).coerceAtMost(1f), coins = progress.coins - cost)
                "focus" -> progress.copy(focus = (progress.focus + 0.05f).coerceAtMost(1f), coins = progress.coins - cost)
                "reflex" -> progress.copy(reflex = (progress.reflex + 0.05f).coerceAtMost(1f), coins = progress.coins - cost)
                "precision" -> progress.copy(precision = (progress.precision + 0.05f).coerceAtMost(1f), coins = progress.coins - cost)
                "breath" -> progress.copy(breathControl = (progress.breathControl + 0.05f).coerceAtMost(1f), coins = progress.coins - cost)
                else -> return@launch
            }
            repository.savePlayerProgress(updated)
        }
    }
}
