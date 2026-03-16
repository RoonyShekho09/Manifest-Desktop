package com.example.myapplication.presentation.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.utils.painter

@Composable
fun AppNavigation(modifier: Modifier = Modifier, startDestination: Any = Screen.Home) {
    val navController = rememberNavController()
    val screens = listOf(Screen.Home, Screen.Drivers, Screen.Cars)

    val state = rememberWideNavigationRailState(initialValue = WideNavigationRailValue.Collapsed)

    Row(modifier = modifier.fillMaxSize()) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        if (currentRoute != Screen.Login::class.qualifiedName) {
            WideNavigationRail(
                colors = WideNavigationRailDefaults.colors(containerColor = Color.Transparent),
                state = state,
                modifier = Modifier.fillMaxHeight()
            ) {
                Spacer(Modifier.height(12.dp))
                screens.forEach { destination ->
                    WideNavigationRailItem(
                        selected = currentRoute == destination::class.qualifiedName,
                        onClick = {
                            navController.navigate(route = destination)
                        },
                        icon = {
                            Icon(
                                painter = destination.icon.painter,
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) },
                        railExpanded = state.targetValue == WideNavigationRailValue.Expanded
                    )
                }
                Spacer(Modifier.weight(1f))
            }
        }

        AppNavGraph(
            navController = navController,
            startDestination = startDestination
        )
    }
}
