package com.jawharat.manifest

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.ic_jawharat
import com.jawharat.manifest.utils.painter

fun main() = application {
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