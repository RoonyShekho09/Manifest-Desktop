package com.jawharat.manifest.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jawharat.manifest.presentation.feature.vehicles.CarsScreen
import com.jawharat.manifest.presentation.feature.drivers.DriversScreen
import com.jawharat.manifest.presentation.feature.home.HomeScreen
import com.jawharat.manifest.presentation.feature.login.LoginScreen
import com.jawharat.manifest.presentation.feature.shared.LocalSnackBarState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Any = Screen.Home
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<Screen.Login> {
            val hostState = LocalSnackBarState.current
            LoginScreen(
                viewModel = koinViewModel(parameters = { parametersOf(hostState) }),
                onNavigateToHome = {
                    navController.navigateTo(
                        route = Screen.Home,
                        popBackStack = true
                    )
                },
            )
        }

        composable<Screen.Home> {
            val hostState = LocalSnackBarState.current
            HomeScreen(
                viewModel = koinViewModel(parameters = { parametersOf(hostState) }),
                onLogout = { navController.navigateTo(Screen.Login, popBackStack = true) },
            )
        }

        composable<Screen.Dispatches> {
            val hostState = LocalSnackBarState.current
            CarsScreen(viewModel = koinViewModel(parameters = { parametersOf(hostState) }))
        }

        composable<Screen.Drivers> {
            val hostState = LocalSnackBarState.current
            DriversScreen(viewModel = koinViewModel(parameters = { parametersOf(hostState) }))
        }
    }
}
