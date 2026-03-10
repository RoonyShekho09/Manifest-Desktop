package com.example.myapplication.presentation.navigation

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import me.sample.library.resources.Res
import me.sample.library.resources.car_svgrepo_com_2
import me.sample.library.resources.ic_home
import me.sample.library.resources.ic_login
import me.sample.library.resources.logo
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
        override val icon = Res.drawable.car_svgrepo_com_2
    }

    @Serializable
    data object Places : Screen {
        override val label = "Places"
        override val icon = Res.drawable.car_svgrepo_com_2
    }
}