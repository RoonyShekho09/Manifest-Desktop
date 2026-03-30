package com.jawharat.manifest.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.domain.entity.Line
import com.jawharat.manifest.domain.entity.VehicleType
import com.jawharat.manifest.presentation.feature.vehicles.DispatchUiState
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.add_new_vehicle
import com.jawharat.manifest.resources.cancel
import com.jawharat.manifest.resources.confirm
import com.jawharat.manifest.resources.driver
import com.jawharat.manifest.resources.edit_vehicle_details
import com.jawharat.manifest.resources.ic_arrow_drop_down
import com.jawharat.manifest.resources.ic_arrow_drop_up
import com.jawharat.manifest.resources.line_label
import com.jawharat.manifest.resources.plate_number
import com.jawharat.manifest.resources.price
import com.jawharat.manifest.resources.save_changes
import com.jawharat.manifest.resources.status
import com.jawharat.manifest.resources.vehicle_type
import com.jawharat.manifest.utils.handPointerHover
import com.jawharat.manifest.utils.painter
import com.jawharat.manifest.utils.string

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDispatchDialog(
    vehicle: DispatchUiState?,
    lines: List<Line>,
    vehiclesTypes: List<VehicleType>,
    drivers: List<Driver>,
    vehicleTypeSearchQuery: TextFieldState,
    driverSearchQuery: TextFieldState,
    isEdit: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (DispatchUiState?) -> Unit,
) {
    val driverName = rememberTextFieldState(initialText = vehicle?.driverName.orEmpty())
    val price = rememberTextFieldState(initialText = vehicle?.price.orEmpty())
    val plateNumber = rememberTextFieldState(initialText = vehicle?.plateNumber.orEmpty())
    var vehicleType by remember { mutableStateOf(VehicleType("", "")) }
    val type = rememberTextFieldState(initialText = vehicle?.type.orEmpty())
    var line by remember { mutableStateOf(vehicle?.line ?: Line("", "")) }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .width(600.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (isEdit) Res.string.edit_vehicle_details.string else Res.string.add_new_vehicle.string,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DropDownTextField(
                        query = driverSearchQuery,
                        data = drivers.map { it.name },
                        selection = driverName.text.toString(),
                        readOnly = isEdit,
                        placeholder = Res.string.driver.string,
                    )

                    AppTextField(
                        state = plateNumber,
                        modifier = Modifier.weight(1f),
                        placeholder = Res.string.plate_number.string
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DropDownTextField(
                        query = vehicleTypeSearchQuery,
                        data = vehiclesTypes.map { it.name },
                        placeholder = Res.string.vehicle_type.string,
                        selection = vehicleType.name
                    )

                    AppTextField(
                        state = type,
                        modifier = Modifier.weight(1f),
                        placeholder = Res.string.status.string
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DropDownTextField(
                        data = lines.map { it.name },
                        readOnly = true,
                        placeholder = Res.string.line_label.string,
                        selection = line.name
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppTextField(
                        state = price,
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.weight(1f),
                        placeholder = Res.string.price.string
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.handPointerHover()
                    ) {
                        Text(text = Res.string.cancel.string, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            if (isEdit)
                                onConfirm(
                                    vehicle?.copy(
                                        plateNumber = plateNumber.text.toString(),
                                        carType = vehicleType.name,
                                        type = type.text.toString(),
                                        line = line,
                                        price = vehicle.price
                                    )
                                )
                            else
                                onConfirm(
                                    DispatchUiState(
                                        driverName = vehicle?.driverName.orEmpty(),
                                        plateNumber = plateNumber.text.toString(),
                                        carType = vehicleType.name,
                                        type = type.text.toString(),
                                        line = line,
                                        price = vehicle?.price.orEmpty()
                                    )
                                )
                        },
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Text(text = if (isEdit) Res.string.save_changes.string else Res.string.confirm.string)
                    }
                }
            }
        }
    }
}

@Composable
fun DropDownTextField(
    query: TextFieldState = rememberTextFieldState(),
    data: List<String>,
    placeholder: String,
    selection: String,
    readOnly: Boolean = false,
    addLimit: Boolean = true,
) {
    var isVehicleTypeDropDownVisible by remember { mutableStateOf(false) }
    var iconPressed by remember { mutableStateOf(false) }

    fun showDropDown() {
        if (data.size <= 50)
            isVehicleTypeDropDownVisible = true
    }

    fun hideDropDown() {
        isVehicleTypeDropDownVisible = false
    }

    LaunchedEffect(query.text) {
        if (query.text.length >= 3)
            showDropDown()
    }

    LaunchedEffect(selection) {
        query.clearText()
        query.edit {
            append(selection)
        }
        hideDropDown()
    }

    Column {
        AppTextField(
            state = query,
            placeholder = placeholder,
            readOnly = readOnly,
            trailingIcon = {
                IconButton(
                    enabled = data.size <= 50 || !addLimit,
                    onClick = { if (isVehicleTypeDropDownVisible) hideDropDown() else showDropDown() },
                    modifier = Modifier
                        .handPointerHover()
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                                    if (event.type == PointerEventType.Press) {
                                        iconPressed = true
                                    }
                                }
                            }
                        }
                ) {
                    Icon(
                        painter = if (isVehicleTypeDropDownVisible)
                            Res.drawable.ic_arrow_drop_up.painter
                        else
                            Res.drawable.ic_arrow_drop_down.painter,
                        contentDescription = null,
                    )
                }
            },
            modifier = Modifier
                .onFocusEvent {
                    if (it.isFocused)
                        showDropDown()
                }
                .pointerInput(isVehicleTypeDropDownVisible) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                            if (event.type == PointerEventType.Release && !iconPressed) {
                                if (isVehicleTypeDropDownVisible) hideDropDown() else showDropDown()
                            }
                        }
                    }
                }
        )
        DropdownMenu(
            expanded = isVehicleTypeDropDownVisible,
            onDismissRequest = {
                if (iconPressed) {
                    iconPressed = false
                } else {
                    hideDropDown()
                }
            },
            properties = PopupProperties(focusable = false),
            modifier = Modifier.heightIn(max = 400.dp)
        ) {
            data.forEach { selection ->
                DropdownMenuItem(
                    text = { Text(text = selection) },
                    onClick = {
                        query.clearText()
                        query.edit {
                            append(selection)
                        }
                        hideDropDown()
                    }
                )
            }
        }
    }
}
