package com.jawharat.manifest.presentation.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.jawharat.manifest.presentation.feature.vehicles.DispatchData

@Composable
fun DropDownTextField(
    value: DispatchData,
    query: TextFieldState = rememberTextFieldState(),
    onQueryChange: (String) -> Unit = {},
    data: List<DispatchData>,
    placeholder: String,
    initialValue: String,
    readOnly: Boolean = false,
    onSelect: (DispatchData) -> Unit,
) {
    var isDropDownExpanded by remember { mutableStateOf(false) }
    var justSelected by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    fun showDropDown() {
        if (data.size <= 50)
            isDropDownExpanded = true
    }

    fun hideDropDown() {
        isDropDownExpanded = false
    }

    LaunchedEffect(query.text) {
        if (justSelected) {
            justSelected = false
            return@LaunchedEffect
        }

        if (query.text.length >= 3 && query.text != initialValue)
            showDropDown()
        else
            hideDropDown()
    }

    LaunchedEffect(initialValue) {
        onQueryChange(initialValue)
        hideDropDown()
        focusRequester.freeFocus()
    }

    val textValue = rememberTextFieldState(value.name)

    LaunchedEffect(textValue.text) {
        query.edit {
            replace(0, length, textValue.text)
        }
    }

    Column {
        AppTextField(
            state = textValue,
            placeholder = placeholder,
            readOnly = readOnly,
            modifier = Modifier
                .focusRequester(focusRequester)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            showDropDown()
                        }
                    }
                }
        )
        DropdownMenu(
            expanded = isDropDownExpanded,
            onDismissRequest = { hideDropDown() },
            properties = PopupProperties(focusable = false),
            modifier = Modifier.heightIn(max = 400.dp)
        ) {
            data.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item.name) },
                    onClick = {
                        onSelect(item)
                        textValue.edit {
                            replace(0, length, item.name)
                        }
                        justSelected = true
                        onQueryChange(item.name)
                        hideDropDown()
                    }
                )
            }
        }
    }
}
