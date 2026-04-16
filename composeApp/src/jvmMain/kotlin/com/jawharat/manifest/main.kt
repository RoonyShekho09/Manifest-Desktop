package com.jawharat.manifest

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.jawharat.manifest.di.dataSourceModule
import com.jawharat.manifest.di.databaseModule
import com.jawharat.manifest.di.networkModule
import com.jawharat.manifest.di.repositoryModule
import com.jawharat.manifest.di.utilModule
import com.jawharat.manifest.di.viewModelModule
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.ic_jawharat
import com.jawharat.manifest.utils.painter
import org.koin.core.context.startKoin

fun main() = application {
    startKoin {
        modules(
            networkModule,
            viewModelModule,
            repositoryModule,
            dataSourceModule,
            databaseModule,
            utilModule
        )
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Manifest",
        resizable = true,
        icon = Res.drawable.ic_jawharat.painter,
        state = rememberWindowState(size = DpSize(1200.dp, 800.dp))
    ) {
        App()
    }
}