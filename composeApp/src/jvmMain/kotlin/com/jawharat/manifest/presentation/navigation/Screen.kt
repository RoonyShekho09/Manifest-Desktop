package com.jawharat.manifest.presentation.navigation

import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.drivers
import com.jawharat.manifest.resources.home
import com.jawharat.manifest.resources.ic_car_wheel
import com.jawharat.manifest.resources.ic_home
import com.jawharat.manifest.resources.ic_login
import com.jawharat.manifest.resources.ic_vehicle
import com.jawharat.manifest.resources.login
import com.jawharat.manifest.resources.logo
import com.jawharat.manifest.resources.vehicles
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

@Serializable
sealed interface Screen {
    val label: StringResource

    @Transient
    val icon: DrawableResource get() = Res.drawable.ic_login

    @Serializable
    data object Login : Screen {
        override val label = Res.string.login
        override val icon = Res.drawable.logo
    }

    @Serializable
    data object Home : Screen {
        override val label = Res.string.home
        override val icon = Res.drawable.ic_home
    }

    @Serializable
    data object Dispatches : Screen {
        override val label = Res.string.vehicles
        override val icon = Res.drawable.ic_vehicle
    }

    @Serializable
    data object Drivers : Screen {
        override val label = Res.string.drivers
        override val icon = Res.drawable.ic_car_wheel
    }
}