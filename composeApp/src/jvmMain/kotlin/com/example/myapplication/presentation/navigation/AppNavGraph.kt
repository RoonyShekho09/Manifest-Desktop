package com.example.myapplication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.presentation.feature.cars.CarsScreen
import com.example.myapplication.presentation.feature.drivers.DriversScreen
import com.example.myapplication.presentation.feature.home.HomeScreen
import com.example.myapplication.presentation.feature.login.LoginScreen
import com.example.myapplication.presentation.feature.shared.LocalSnackBarState
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
                viewModel = koinViewModel(parameters = { parametersOf(hostState) })
            )
        }

        composable<Screen.Cars> {
            val hostState = LocalSnackBarState.current
            CarsScreen(viewModel = koinViewModel(parameters = { parametersOf(hostState) }))
        }

        composable<Screen.Drivers> {
            val hostState = LocalSnackBarState.current
            DriversScreen(viewModel = koinViewModel(parameters = { parametersOf(hostState) }))
        }
    }
}
