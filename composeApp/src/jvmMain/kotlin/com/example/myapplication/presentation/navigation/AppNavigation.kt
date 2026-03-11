package com.example.myapplication.presentation.navigation

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    val screens = listOf(Screen.Home, Screen.Drivers, Screen.Cars)

    val state = rememberWideNavigationRailState(initialValue = WideNavigationRailValue.Collapsed)
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

//    LaunchedEffect(isHovered) {
//        if (isHovered)
//            state.expand()
//        else
//            state.collapse()
//    }

    Row(modifier = modifier.fillMaxSize()) {
        WideNavigationRail(
            colors = WideNavigationRailDefaults.colors(containerColor = Color.Transparent),
            state = state,
            modifier = Modifier
                .hoverable(interactionSource)
                .fillMaxHeight()
        ) {
            Spacer(Modifier.height(12.dp))

            screens.forEach { destination ->
                WideNavigationRailItem(
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
                    label = { Text(destination.label) },
                    railExpanded = state.targetValue == WideNavigationRailValue.Expanded
                )
            }

            Spacer(Modifier.weight(1f))
        }

        AppNavGraph(
            navController = navController,
            startDestination = startDestination
        )
    }
}
