package com.example.myapplication.presentation.navigation

import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.ic_car_wheel
import com.jawharat.manifest.resources.ic_home
import com.jawharat.manifest.resources.ic_login
import com.jawharat.manifest.resources.ic_vehicle
import com.jawharat.manifest.resources.logo
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.DrawableResource

@Serializable
sealed interface Screen {
    val label: String

    @Transient
    val icon: DrawableResource get() = Res.drawable.ic_login

    @Serializable
    data object Login : Screen {
        override val label = "Login"
        override val icon = Res.drawable.logo
    }

    @Serializable
    data object Home : Screen {
        override val label = "Home"
        override val icon = Res.drawable.ic_home
    }

    @Serializable
    data object Cars : Screen {
        override val label = "Cars"
        override val icon = Res.drawable.ic_vehicle
    }

    @Serializable
    data object Drivers : Screen {
        override val label = "Drivers"
        override val icon = Res.drawable.ic_car_wheel
    }
}