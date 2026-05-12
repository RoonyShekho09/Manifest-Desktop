package com.jawharat.manifest.utils

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
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

fun Modifier.fillHeightOfParent(parentPadding: Dp) = this.then(
    layout { measurable, constraints ->
        val paddingPx = parentPadding.roundToPx()
        val newMaxHeight =
            (constraints.maxHeight + 2 * paddingPx).coerceAtLeast(constraints.minHeight)
        val newMaxWidth = (constraints.maxWidth + 2 * paddingPx).coerceAtLeast(constraints.minWidth)

        val placeable = measurable.measure(
            constraints.copy(
                maxHeight = newMaxHeight,
                maxWidth = newMaxWidth
            ),
        )
        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
)

fun Modifier.handPointerHover() = this.pointerHoverIcon(PointerIcon.Hand)

fun Modifier.handClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
) = composed {
    this
        .clickable(
            enabled = enabled,
            onClickLabel = onClickLabel,
            role = role,
            onClick = onClick
        )
        .pointerHoverIcon(if (enabled) PointerIcon.Hand else PointerIcon.Default)
}

fun Modifier.moveFocusOnEnter(focusManager: FocusManager): Modifier = this.onKeyEvent {
    if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
        focusManager.moveFocus(FocusDirection.Next)
        true
    } else {
        false
    }
}
