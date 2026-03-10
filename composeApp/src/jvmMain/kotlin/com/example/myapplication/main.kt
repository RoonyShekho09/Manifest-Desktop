package com.example.myapplication

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.example.myapplication.utils.painter
import me.sample.library.resources.Res
import me.sample.library.resources.ic_check
import me.sample.library.resources.ic_jawharat

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Manifest",
        resizable = false,
        icon = Res.drawable.ic_jawharat.painter,
        state = rememberWindowState(size = DpSize(900.dp, 700.dp))
    ) {
        App()
    }
}