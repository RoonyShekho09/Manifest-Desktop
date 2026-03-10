package com.example.myapplication.presentation.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.utils.painter

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Screen.Home
    var selectedDestination by rememberSaveable { mutableStateOf<Screen>(startDestination) }
    val screens = listOf(Screen.Home, Screen.Places, Screen.Cars)

    Row(modifier = modifier.fillMaxSize()) {
        NavigationRail(containerColor = Color.Transparent) {
            Spacer(Modifier.height(12.dp))

            screens.forEach { destination ->
                NavigationRailItem(
                    colors = NavigationRailItemDefaults.colors(),
                    selected = selectedDestination == destination,
                    onClick = {
                        navController.navigate(route = destination)
                        selectedDestination = destination
                    },
                    icon = {
                        Icon(
                            painter = destination.icon.painter,
                            contentDescription = destination.label
                        )
                    },
                    label = { Text(destination.label) }
                )
            }
        }

        AppNavGraph(
            navController = navController,
            startDestination = startDestination
        )
    }
}
