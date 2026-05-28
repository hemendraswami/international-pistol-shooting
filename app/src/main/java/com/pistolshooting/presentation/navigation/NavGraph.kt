package com.pistolshooting.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pistolshooting.domain.model.ShootingMode
import com.pistolshooting.presentation.screens.career.CareerScreen
import com.pistolshooting.presentation.screens.game.GameScreen
import com.pistolshooting.presentation.screens.home.HomeScreen
import com.pistolshooting.presentation.screens.modeselect.LevelSelectScreen
import com.pistolshooting.presentation.screens.modeselect.ModeSelectScreen
import com.pistolshooting.presentation.screens.profile.ProfileScreen
import com.pistolshooting.presentation.screens.results.ResultsScreen
import com.pistolshooting.presentation.screens.splash.SplashScreen
import com.pistolshooting.presentation.screens.weapons.WeaponSelectScreen

@Composable
fun PistolShootingNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onNavigateToHome = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToModeSelect = { navController.navigate(Screen.ModeSelect.route) },
                onNavigateToCareer = { navController.navigate(Screen.Career.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToWeapons = { navController.navigate(Screen.WeaponSelect.route) }
            )
        }

        composable(Screen.ModeSelect.route) {
            ModeSelectScreen(
                onSelectMode = { mode ->
                    navController.navigate(Screen.LevelSelect.createRoute(mode.name))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.WeaponSelect.route) {
            WeaponSelectScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.LevelSelect.route,
            arguments = listOf(navArgument("modeId") { type = NavType.StringType })
        ) { backStack ->
            val modeId = backStack.arguments?.getString("modeId") ?: ShootingMode.AIR_PISTOL_10M.name
            val mode = runCatching { ShootingMode.valueOf(modeId) }.getOrDefault(ShootingMode.AIR_PISTOL_10M)
            LevelSelectScreen(
                shootingMode = mode,
                onSelectLevel = { levelNumber ->
                    navController.navigate(Screen.Game.createRoute(levelNumber))
                },
                onStartPractice = {
                    navController.navigate(Screen.PracticeGame.createRoute(mode.name))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(navArgument("levelNumber") { type = NavType.IntType })
        ) { backStack ->
            val levelNumber = backStack.arguments?.getInt("levelNumber") ?: 1
            GameScreen(
                levelNumber = levelNumber,
                onGameComplete = {
                    navController.navigate(Screen.Results.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PracticeGame.route,
            arguments = listOf(navArgument("modeId") { type = NavType.StringType })
        ) { backStack ->
            val modeId = backStack.arguments?.getString("modeId") ?: ShootingMode.AIR_PISTOL_10M.name
            val mode = runCatching { ShootingMode.valueOf(modeId) }.getOrDefault(ShootingMode.AIR_PISTOL_10M)
            GameScreen(
                levelNumber = -1,
                practiceMode = mode,
                onGameComplete = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Results.route) {
            ResultsScreen(
                onPlayAgain = { navController.popBackStack(Screen.ModeSelect.route, false) },
                onGoHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Career.route) {
            CareerScreen(
                onSelectLevel = { levelNumber ->
                    navController.navigate(Screen.Game.createRoute(levelNumber))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
