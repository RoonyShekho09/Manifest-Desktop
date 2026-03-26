package com.jawharat.manifest.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun AppTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    readOnly: Boolean = false,
    enabled: Boolean = true,
    suffixText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    outputTransformation: OutputTransformation? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    inputTransformation: InputTransformation? = null
) {
    BasicTextField(
        state = state,
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp
        ),
        keyboardOptions = keyboardOptions,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        onKeyboardAction = onKeyboardAction,
        outputTransformation = outputTransformation,
        inputTransformation = inputTransformation,
        lineLimits = TextFieldLineLimits.SingleLine,
        readOnly = readOnly,
        enabled = enabled,
        modifier = modifier.defaultMinSize(minHeight = 40.dp),
        decorator = object : TextFieldDecorator {
            @Composable
            override fun Decoration(innerTextField: @Composable () -> Unit) {
                TextFieldDecorator(
                    state = state,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    suffixText = suffixText,
                    innerTextField = innerTextField
                )
            }
        }
    )
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Enter text here...",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    suffixText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    outputTransformation: OutputTransformation? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    inputTransformation: InputTransformation? = null
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp
        ),
        keyboardOptions = keyboardOptions,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        readOnly = readOnly,
        enabled = enabled,
        modifier = modifier.defaultMinSize(minHeight = 40.dp),
        decorationBox = {
            TextFieldDecorator(
                value = value,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                suffixText = suffixText,
                innerTextField = it
            )
        }
//        decorationBox = object : TextFieldDecorator {
//            @Composable
//            override fun Decoration(innerTextField: @Composable () -> Unit) {
//                TextFieldDecorator(
//                    value = value,
//                    placeholder = placeholder,
//                    leadingIcon = leadingIcon,
//                    trailingIcon = trailingIcon,
//                    suffixText = suffixText,
//                    innerTextField = innerTextField
//                )
//            }
//        },
    )
}

@Composable
private fun TextFieldDecorator(
    value: String,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    suffixText: String?,
    innerTextField: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            leadingIcon?.invoke()
            if (leadingIcon != null) Spacer(Modifier.width(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (value.isBlank()) {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
                innerTextField()
            }

            suffixText?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 14.sp
                )
            }
            trailingIcon?.invoke()
        }
    }
}

@Composable
private fun TextFieldDecorator(
    state: TextFieldState,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    suffixText: String?,
    innerTextField: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            leadingIcon?.invoke()
            if (leadingIcon != null) Spacer(Modifier.width(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (state.text.isBlank()) {
                    Text(
                        text = placeholder,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
                innerTextField()
            }

            suffixText?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 14.sp
                )
            }
            trailingIcon?.invoke()
        }
    }
}
