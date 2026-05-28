package com.pistolshooting.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash         : Screen("splash")
    data object Home           : Screen("home")
    data object ModeSelect     : Screen("mode_select")
    data object WeaponSelect   : Screen("weapon_select")
    data object LevelSelect    : Screen("level_select/{modeId}") {
        fun createRoute(modeId: String) = "level_select/$modeId"
    }
    data object Game           : Screen("game/{levelNumber}") {
        fun createRoute(levelNumber: Int) = "game/$levelNumber"
    }
    data object PracticeGame   : Screen("practice_game/{modeId}") {
        fun createRoute(modeId: String) = "practice_game/$modeId"
    }
    data object Results        : Screen("results")
    data object Profile        : Screen("profile")
    data object Career         : Screen("career")
    data object Settings       : Screen("settings")
}
