package com.jawharat.manifest.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.RadioButton
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
import com.jawharat.manifest.domain.entity.Line
import com.jawharat.manifest.domain.entity.VehicleType
import com.jawharat.manifest.presentation.feature.vehicles.DispatchUiState
import com.jawharat.manifest.presentation.feature.vehicles.DriverStatus
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.add_new_vehicle
import com.jawharat.manifest.resources.cancel
import com.jawharat.manifest.resources.confirm
import com.jawharat.manifest.resources.driver
import com.jawharat.manifest.resources.edit_vehicle_details
import com.jawharat.manifest.resources.ic_arrow_drop_down
import com.jawharat.manifest.resources.ic_arrow_drop_up
import com.jawharat.manifest.resources.inside
import com.jawharat.manifest.resources.line_label
import com.jawharat.manifest.resources.outside
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
    vehicleTypeSearchQuery: TextFieldState,
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
    var status by remember { mutableStateOf(vehicle?.status) }

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
                    AppTextField(
                        state = driverName,
                        readOnly = isEdit,
                        modifier = Modifier.weight(1f),
                        placeholder = Res.string.driver.string
                    )
                    AppTextField(
                        state = plateNumber,
                        modifier = Modifier.weight(1f),
                        placeholder = Res.string.plate_number.string
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    var isVehicleTypeDropDownVisible by remember { mutableStateOf(false) }
                    var iconPressed by remember { mutableStateOf(false) }

                    LaunchedEffect(vehicleTypeSearchQuery.text) {
                        if (vehicleTypeSearchQuery.text.length >= 3) {
                            isVehicleTypeDropDownVisible = true
                        }
                    }

                    LaunchedEffect(vehicleType) {
                        vehicleTypeSearchQuery.clearText()
                        vehicleTypeSearchQuery.edit {
                            append(vehicleType.name)
                        }
                        isVehicleTypeDropDownVisible = false
                    }

                    Column {
                        AppTextField(
                            state = vehicleTypeSearchQuery,
                            placeholder = Res.string.vehicle_type.string,
                            trailingIcon = {
                                IconButton(
                                    onClick = { isVehicleTypeDropDownVisible = !isVehicleTypeDropDownVisible },
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
                                        isVehicleTypeDropDownVisible = true
                                }
                        )
                        DropdownMenu(
                            expanded = isVehicleTypeDropDownVisible,
                            onDismissRequest = {
                                if (iconPressed) {
                                    iconPressed = false
                                } else {
                                    isVehicleTypeDropDownVisible = false
                                }
                            },
                            properties = PopupProperties(focusable = false)
                        ) {
                            vehiclesTypes.forEach { selection ->
                                DropdownMenuItem(
                                    text = { Text(text = selection.name) },
                                    onClick = {
                                        vehicleTypeSearchQuery.clearText()
                                        vehicleTypeSearchQuery.edit {
                                            append(selection.name)
                                        }
                                        isVehicleTypeDropDownVisible = false
                                    }
                                )
                            }
                        }
                    }

                    AppTextField(
                        state = type,
                        modifier = Modifier.weight(1f),
                        placeholder = Res.string.status.string
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    var isLineDropDownVisible by remember { mutableStateOf(false) }

                    Column {
                        Box(modifier = Modifier.clickable { isLineDropDownVisible = true }) {
                            AppTextField(
                                value = line.name,
                                onValueChange = { },
                                placeholder = Res.string.line_label.string,
                                readOnly = false,
                                enabled = false
                            )

                            Box(modifier = Modifier.matchParentSize().handPointerHover())
                        }

                        DropdownMenu(
                            expanded = isLineDropDownVisible,
                            onDismissRequest = { isLineDropDownVisible = false }
                        ) {
                            lines.forEach { selection ->
                                DropdownMenuItem(
                                    text = { Text(text = selection.name) },
                                    onClick = {
                                        line = selection
                                        isLineDropDownVisible = false
                                    }
                                )
                            }
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppTextField(
                        state = price,
                        readOnly = isEdit,
                        modifier = Modifier.weight(1f),
                        placeholder = Res.string.price.string
                    )

                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${Res.string.status.string}: ",
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.width(8.dp))
                        RadioButton(
                            selected = status == DriverStatus.INSIDE,
                            onClick = { status = DriverStatus.INSIDE }
                        )
                        Text(text = Res.string.inside.string)
                        Spacer(Modifier.width(8.dp))
                        RadioButton(
                            selected = status == DriverStatus.OUTSIDE,
                            onClick = { status = DriverStatus.OUTSIDE },
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                        )
                        Text(text = Res.string.outside.string)
                    }
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
                                        status = status ?: DriverStatus.INSIDE,
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
                                        status = status ?: DriverStatus.INSIDE,
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
