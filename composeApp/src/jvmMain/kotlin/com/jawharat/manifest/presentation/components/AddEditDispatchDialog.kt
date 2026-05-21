package com.jawharat.manifest.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jawharat.manifest.domain.entity.manifest.DispatchLine
import com.jawharat.manifest.domain.entity.manifest.Driver
import com.jawharat.manifest.domain.entity.Route
import com.jawharat.manifest.domain.entity.manifest.VehicleType
import com.jawharat.manifest.presentation.feature.shared.SearchState
import com.jawharat.manifest.presentation.feature.vehicles.DispatchData
import com.jawharat.manifest.presentation.feature.vehicles.DispatchUiState
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.add_new_vehicle
import com.jawharat.manifest.resources.cancel
import com.jawharat.manifest.resources.car_type
import com.jawharat.manifest.resources.confirm
import com.jawharat.manifest.resources.driver
import com.jawharat.manifest.resources.edit_vehicle_details
import com.jawharat.manifest.resources.line_label
import com.jawharat.manifest.resources.plate_number
import com.jawharat.manifest.resources.price
import com.jawharat.manifest.resources.save_changes
import com.jawharat.manifest.resources.vehicle_type
import com.jawharat.manifest.utils.handPointerHover
import com.jawharat.manifest.utils.moveFocusOnEnter
import com.jawharat.manifest.utils.string

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDispatchDialog(
    dispatchToEdit: DispatchUiState?,
    vehicleTypes: List<VehicleType>,
    routes: List<Route>?,
    dispatchLines: List<DispatchLine>,
    driverSearchState: SearchState<Driver>,
    onDismiss: () -> Unit,
    onConfirm: (DispatchUiState?) -> Unit,
) {
    var driver by remember { mutableStateOf(dispatchToEdit?.driver ?: DispatchData()) }
    var price by remember { mutableStateOf(dispatchToEdit?.price.orEmpty()) }
    val plateNumber = rememberTextFieldState(initialText = dispatchToEdit?.plateNumber.orEmpty())
    var vehicleName by remember { mutableStateOf(dispatchToEdit?.vehicleName.orEmpty()) }
    var vehicleType by remember {
        mutableStateOf(
            DispatchData(
                name = dispatchToEdit?.vehicleType.orEmpty(),
                id = dispatchToEdit?.id.orEmpty()
            )
        )
    }
    var line by remember { mutableStateOf(dispatchToEdit?.line ?: DispatchData()) }
    val isEdit = dispatchToEdit != null

    val isConfirmEnabled = driver.name.isNotBlank() && price.isNotBlank()
            && plateNumber.text.isNotBlank() && vehicleName.isNotBlank()
            && vehicleType.name.isNotBlank()
            && driverSearchState.searchResults.any { it.name == driverSearchState.query.text.trimEnd() }
            && vehicleTypes.any { it.name == vehicleType.name }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(line, vehicleType) {
        val foundPrice = routes?.find { it.routeName == line.name }
            ?.prices?.find { it.type.displayName == vehicleType.name }
            ?.price?.toString()
        price = foundPrice.orEmpty()
    }


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

                AppTextField(
                    state = plateNumber,
                    placeholder = Res.string.plate_number.string,
                    modifier = Modifier.moveFocusOnEnter(focusManager)
                )

                AppTextField(
                    value = vehicleName,
                    placeholder = Res.string.vehicle_type.string,
                    onValueChange = { vehicleName = it },
                    modifier = Modifier.moveFocusOnEnter(focusManager)
                )

                DropDownTextField(
                    value = driver,
                    query = driverSearchState.query,
                    data = driverSearchState.searchResults.map {
                        DispatchData(
                            id = it.id,
                            name = it.name
                        )
                    },
                    placeholder = Res.string.driver.string,
                    initialValue = driver.name,
                    onSelect = { driver = it },
                )

                DropDownTextField(
                    value = line,
                    data = dispatchLines.map { DispatchData(id = it.id, name = it.name) },
                    readOnly = true,
                    placeholder = Res.string.line_label.string,
                    initialValue = line.name,
                    onSelect = { line = it }
                )

                DropDownTextField(
                    value = vehicleType,
                    readOnly = true,
                    data = vehicleTypes.map { DispatchData(id = it.id, name = it.name) },
                    placeholder = Res.string.car_type.string,
                    initialValue = vehicleType.name,
                    onSelect = { vehicleType = it }
                )

                AppTextField(
                    value = price,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    placeholder = Res.string.price.string,
                    modifier = Modifier.moveFocusOnEnter(focusManager)
                )

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
                                    dispatchToEdit.copy(
                                        driver = driver,
                                        plateNumber = plateNumber.text.toString(),
                                        vehicleName = vehicleName,
                                        vehicleType = vehicleType.name,
                                        line = line,
                                        price = price
                                    )
                                )
                            else
                                onConfirm(
                                    DispatchUiState(
                                        driver = driver,
                                        plateNumber = plateNumber.text.toString(),
                                        vehicleName = vehicleName,
                                        price = price,
                                        vehicleType = vehicleType.name,
                                        line = line,
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
