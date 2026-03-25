package com.jawharat.manifest.presentation.navigation

import androidx.navigation.NavHostController

fun <T : Any> NavHostController.navigateTo(route: T, popBackStack: Boolean = false) {
    navigate(
        route = route,
        builder = {
            if (popBackStack) {
                popUpTo(0) {
                    inclusive = true
                    saveState = true
                }
                restoreState = true
                launchSingleTop = true
            }
        }
    )
}