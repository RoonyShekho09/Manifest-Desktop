package com.example.myapplication.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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

/**
 * @param state The current textfield state of the field.
 * @param modifier The modifier to be applied to the composable.
 * @param placeholder The placeholder text to display when the field is empty.
 * @param keyboardOptions Options for the software keyboard (e.g., KeyboardType.Text).
 * @param leadingIcon A composable function for an optional icon/content at the start of the field.
 * @param inputTransformation The inputTransformation transformation applied to the text (e.g., for passwords).
 */
@Composable
fun AppTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    placeholder: String = "Enter text here...",
    suffixText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    outputTransformation: OutputTransformation? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    inputTransformation: InputTransformation = InputTransformation
) {
    BasicTextField(
        state = state,
        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
        keyboardOptions = keyboardOptions,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        onKeyboardAction = onKeyboardAction,
        outputTransformation = outputTransformation,
        inputTransformation = inputTransformation,
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = modifier.height(56.dp),
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
private fun TextFieldDecorator(
    state: TextFieldState,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    suffixText: String?,
    innerTextField: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp).height(56.dp) // Move height here
        ) {
            leadingIcon?.invoke()
            if (leadingIcon != null) Spacer(Modifier.width(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (state.text.isBlank()) {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                innerTextField()
            }

            suffixText?.let {
                Text(text = it, modifier = Modifier.padding(end = 16.dp))
            }
            trailingIcon?.invoke()
        }
    }
}