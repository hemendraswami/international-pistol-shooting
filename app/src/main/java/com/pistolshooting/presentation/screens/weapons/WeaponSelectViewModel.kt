package com.pistolshooting.presentation.screens.weapons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pistolshooting.data.local.entity.PlayerProgressEntity
import com.pistolshooting.domain.model.WeaponType
import com.pistolshooting.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeaponUiState(
    val progress: PlayerProgressEntity? = null,
    val selectedWeapon: WeaponType = WeaponType.MORINI_162E,
    val unlockedWeapons: Set<String> = setOf("MORINI_162E"),
    val isLoading: Boolean = true
)

@HiltViewModel
class WeaponSelectViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    val uiState: StateFlow<WeaponUiState> = repository.observePlayerProgress()
        .filterNotNull()
        .map { progress ->
            WeaponUiState(
                progress = progress,
                selectedWeapon = runCatching { WeaponType.valueOf(progress.selectedWeaponName) }
                    .getOrDefault(WeaponType.MORINI_162E),
                unlockedWeapons = progress.unlockedWeapons.split(",").toSet(),
                isLoading = false
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WeaponUiState())

    fun selectWeapon(weapon: WeaponType) {
        viewModelScope.launch {
            val progress = repository.getDefaultPlayerProgress()
            repository.savePlayerProgress(progress.copy(selectedWeaponName = weapon.name))
        }
    }

    fun purchaseWeapon(weapon: WeaponType) {
        viewModelScope.launch {
            val progress = repository.getDefaultPlayerProgress()
            if (progress.coins < weapon.cost) return@launch
            val newUnlocked = (progress.unlockedWeapons.split(",") + weapon.name).distinct().joinToString(",")
            repository.savePlayerProgress(
                progress.copy(
                    coins = progress.coins - weapon.cost,
                    unlockedWeapons = newUnlocked,
                    selectedWeaponName = weapon.name
                )
            )
        }
    }
}
