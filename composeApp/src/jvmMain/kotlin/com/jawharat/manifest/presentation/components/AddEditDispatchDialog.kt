package com.jawharat.manifest.presentation.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
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
import com.jawharat.manifest.resources.line_label
import com.jawharat.manifest.resources.plate_number
import com.jawharat.manifest.resources.price
import com.jawharat.manifest.resources.save_changes
import com.jawharat.manifest.resources.status
import com.jawharat.manifest.resources.vehicle_type
import com.jawharat.manifest.utils.handPointerHover
import com.jawharat.manifest.utils.string

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDispatchDialog(
    vehicleToEdit: DispatchUiState?,
    lines: List<Line>,
    vehiclesTypes: List<VehicleType>,
    drivers: List<Driver>,
    vehicleTypeSearchQuery: String,
    driverSearchQuery: String,
    onDismiss: () -> Unit,
    onConfirm: (DispatchUiState?) -> Unit,
    onVehicleTypeSearchQueryChange: (String) -> Unit,
    onDriverSearchQueryChange: (String) -> Unit,
) {
    val driverName by remember { mutableStateOf(vehicleToEdit?.driverName.orEmpty()) }
    val price = rememberTextFieldState(initialText = vehicleToEdit?.price.orEmpty())
    val plateNumber = rememberTextFieldState(initialText = vehicleToEdit?.plateNumber.orEmpty())
    var vehicleType by remember { mutableStateOf(vehicleToEdit?.vehicleType.orEmpty()) }
    val type = rememberTextFieldState(initialText = vehicleToEdit?.type.orEmpty())
    var line by remember { mutableStateOf(vehicleToEdit?.line ?: Line("", "")) }
    val isConfirmEnabled by rememberUpdatedState(
        driverName.isNotBlank() && price.text.isNotBlank()
                && plateNumber.text.isNotBlank() && vehicleType.isNotBlank()
                && type.text.isNotBlank()
    )

    val isEdit = vehicleToEdit != null

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
                        placeholder = Res.string.driver.string,
                        initialValue = driverName,
                        onQueryChange = onDriverSearchQueryChange,
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
                        initialValue = vehicleType,
                        onQueryChange = onVehicleTypeSearchQueryChange
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
                        initialValue = line.name
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
                                    vehicleToEdit.copy(
                                        plateNumber = plateNumber.text.toString(),
                                        vehicleType = vehicleType,
                                        type = type.text.toString(),
                                        line = line,
                                        price = vehicleToEdit.price,
                                        driverName = driverName
                                    )
                                )
                            else
                                onConfirm(
                                    DispatchUiState(
                                        driverName = vehicleToEdit?.driverName.orEmpty(),
                                        plateNumber = plateNumber.text.toString(),
                                        vehicleType = vehicleType,
                                        type = type.text.toString(),
                                        line = line,
                                        price = vehicleToEdit?.price.orEmpty()
                                    )
                                )
                        },
                        enabled = isConfirmEnabled,
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
private fun DropDownTextField(
    query: String = "",
    onQueryChange: (String) -> Unit = {},
    data: List<String>,
    placeholder: String,
    initialValue: String,
    readOnly: Boolean = false,
) {
    var isVehicleTypeDropDownVisible by remember { mutableStateOf(false) }
    var justSelected by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    fun showDropDown() {
        if (data.size <= 50)
            isVehicleTypeDropDownVisible = true
    }

    fun hideDropDown() {
        isVehicleTypeDropDownVisible = false
    }

    LaunchedEffect(query) {
        if (justSelected) {
            justSelected = false
            return@LaunchedEffect
        }

        if (query.length >= 3 && query != initialValue)
            showDropDown()
        else
            hideDropDown()
    }

    LaunchedEffect(initialValue) {
        onQueryChange(initialValue)
        hideDropDown()
        focusRequester.freeFocus()
    }

    Column {
        AppTextField(
            value = query,
            onValueChange = onQueryChange,
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
            expanded = isVehicleTypeDropDownVisible,
            onDismissRequest = { hideDropDown() },
            properties = PopupProperties(focusable = false),
            modifier = Modifier.heightIn(max = 400.dp)
        ) {
            data.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        justSelected = true
                        onQueryChange(item)
                        hideDropDown()
                    }
                )
            }
        }
    }
}
