package com.example.myapplication.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.feature.vehicles.VehicleUiState
import com.example.myapplication.presentation.feature.vehicles.DriverStatus

@Composable
fun EditCarDialog(
    car: VehicleUiState,
    onDismiss: () -> Unit,
    onSave: (VehicleUiState) -> Unit
) {
    val driverName = rememberTextFieldState(initialText = car.driverName)
    val plateNumber = rememberTextFieldState(initialText = car.plateNumber)
    val carType = rememberTextFieldState(initialText = car.carType)
    val type = rememberTextFieldState(initialText = car.type)
    val price = rememberTextFieldState(initialText = car.price)
    val line = rememberTextFieldState(initialText = car.line)
    var status by remember { mutableStateOf(car.status) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
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
                    text = "Edit Driver Details",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppTextField(driverName, modifier = Modifier.weight(1f))
                    AppTextField(plateNumber, modifier = Modifier.weight(1f))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppTextField(carType, modifier = Modifier.weight(1f))
                    AppTextField(type, modifier = Modifier.weight(1f))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppTextField(line, modifier = Modifier.weight(1f))
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppTextField(price, modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Status: ",
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.width(8.dp))
                        RadioButton(
                            selected = status == DriverStatus.INSIDE,
                            onClick = { status = DriverStatus.INSIDE }
                        )
                        Text("Inside")
                        Spacer(Modifier.width(8.dp))
                        RadioButton(
                            selected = status == DriverStatus.OUTSIDE,
                            onClick = { status = DriverStatus.OUTSIDE }
                        )
                        Text("Outside")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            onSave(
                                car.copy(
                                    driverName = driverName.text.toString(),
                                    plateNumber = plateNumber.text.toString(),
                                    carType = carType.text.toString(),
                                    type = type.text.toString(),
                                    line = line.text.toString(),
                                    status = status,
                                )
                            )
                        }
                    ) {
                        Text("Save Changes")
                    }
                }
            }
        }
    }
}
