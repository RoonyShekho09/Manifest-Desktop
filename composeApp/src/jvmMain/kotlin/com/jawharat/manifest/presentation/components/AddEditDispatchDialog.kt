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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.domain.entity.Line
import com.jawharat.manifest.domain.entity.Route
import com.jawharat.manifest.domain.entity.VehicleType
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
import com.jawharat.manifest.utils.string

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDispatchDialog(
    dispatchToEdit: DispatchUiState?,
    carTypes: List<VehicleType>,
    routes: List<Route>?,
    lines: List<Line>,
    driverSearchState: SearchState<Driver>,
    onDismiss: () -> Unit,
    onConfirm: (DispatchUiState?) -> Unit,
) {
    var driver by remember { mutableStateOf(dispatchToEdit?.driver ?: DispatchData()) }
    var price by remember { mutableStateOf(dispatchToEdit?.price.orEmpty()) }
    val plateNumber = rememberTextFieldState(initialText = dispatchToEdit?.plateNumber.orEmpty())
    var vehicleName by remember { mutableStateOf(dispatchToEdit?.vehicleName.orEmpty()) }
    var vehicleType by remember { mutableStateOf(DispatchData(name = dispatchToEdit?.vehicleType.orEmpty())) }
    var line by remember { mutableStateOf(dispatchToEdit?.line ?: DispatchData()) }
    val isConfirmEnabled by rememberUpdatedState(
        driver.name.isNotBlank() && price.isNotBlank()
                && plateNumber.text.isNotBlank() && vehicleName.isNotBlank()
                && vehicleType.name.isNotBlank()
    )

    val isEdit = dispatchToEdit != null

    LaunchedEffect(line.name, vehicleType.name) {
        val currentRoutes = routes ?: return@LaunchedEffect

        val foundPrice = currentRoutes
            .firstOrNull { it.routeName == line.name }
            ?.prices
            ?.firstOrNull { it.type.displayName == vehicleType.name }
            ?.price
            ?.toString()

        if (foundPrice != null) {
            price = foundPrice
        }
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
                    placeholder = Res.string.plate_number.string
                )

                AppTextField(
                    value = vehicleName,
                    placeholder = Res.string.vehicle_type.string,
                    onValueChange = { vehicleName = it }
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
                    onSelect = { driver = it
                    }
                )

                DropDownTextField(
                    value = line,
                    data = lines.map { DispatchData(id = it.id, name = it.name) },
                    readOnly = true,
                    placeholder = Res.string.line_label.string,
                    initialValue = line.name,
                    onSelect = { line = it }
                )

                DropDownTextField(
                    value = vehicleType,
                    data = carTypes.map { DispatchData(id = it.id, name = it.name) },
                    placeholder = Res.string.car_type.string,
                    initialValue = vehicleType.name,
                    onSelect = { vehicleType = it }
                )

                AppTextField(
                    value = price,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    placeholder = Res.string.price.string
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
                            onConfirm(
                                dispatchToEdit?.copy(
                                    driver = driver,
                                    plateNumber = plateNumber.text.toString(),
                                    vehicleName = vehicleName,
                                    vehicleType = vehicleType.name,
                                    line = line,
                                    price = price
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
