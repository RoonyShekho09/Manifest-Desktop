package com.jawharat.manifest.presentation.navigation

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jawharat.manifest.presentation.feature.shared.LocalSnackBarState
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.session_expired
import com.jawharat.manifest.data.remote.observer.AuthEvent
import com.jawharat.manifest.data.remote.observer.AuthObserver
import com.jawharat.manifest.utils.Listen
import com.jawharat.manifest.utils.painter
import com.jawharat.manifest.utils.string
import org.koin.compose.koinInject

@Composable
fun AppNavigation(modifier: Modifier = Modifier, startDestination: Screen = Screen.Home) {
    val navController = rememberNavController()
    val screens = listOf(Screen.Home, Screen.Drivers, Screen.Dispatches)

    val state = rememberWideNavigationRailState(initialValue = WideNavigationRailValue.Collapsed)

    AuthExceptionRouterHandler(
        onTokenExpiredException = {
            navController.navigateTo(
                route = Screen.Login,
                popBackStack = true
            )
        }
    )

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
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                        selected = currentRoute == destination::class.qualifiedName,
                        onClick = {
                            val isCurrentDestination =
                                destination::class.qualifiedName == currentRoute
                            if (!isCurrentDestination)
                                navController.navigateTo(route = destination, popBackStack = true)
                        },
                        icon = {
                            Icon(
                                painter = destination.icon.painter,
                                contentDescription = destination.label.string
                            )
                        },
                        label = { Text(text = destination.label.string) },
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

@Composable
private fun AuthExceptionRouterHandler(onTokenExpiredException: () -> Unit) {
    val authObserver = koinInject<AuthObserver>()
    val event by authObserver.events.collectAsState(null)
    val snackBarHostState = LocalSnackBarState.current
    val scope = rememberCoroutineScope()

    event.Listen {
        if (it == AuthEvent.TokenExpired) {
            onTokenExpiredException()
            snackBarHostState.showFailure(
                message = Res.string.session_expired,
                scope = scope
            )
        }
    }
}
