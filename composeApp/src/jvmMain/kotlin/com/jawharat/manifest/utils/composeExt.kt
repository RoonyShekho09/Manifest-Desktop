package com.jawharat.manifest.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

val DrawableResource.painter: Painter
    @Composable
    get() = painterResource(resource = this)

@Composable
fun StringResource.string(vararg formatArgs: Any): String =
    stringResource(resource = this, formatArgs = formatArgs)

val StringResource.string: String
    @Composable
    get() = stringResource(resource = this)

@Composable
fun <E> E.Listen(onEvent: suspend CoroutineScope.(currentEvent: E) -> Unit) {
    LaunchedEffect(key1 = this) {
        onEvent(this@Listen)
    }
}
