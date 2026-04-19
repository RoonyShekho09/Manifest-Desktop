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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.add_new_driver
import com.jawharat.manifest.resources.cancel
import com.jawharat.manifest.resources.confirm
import com.jawharat.manifest.resources.destination
import com.jawharat.manifest.resources.driver
import com.jawharat.manifest.resources.driver_id_number
import com.jawharat.manifest.resources.driver_phone_number
import com.jawharat.manifest.resources.edit_driver_details
import com.jawharat.manifest.resources.save_changes
import com.jawharat.manifest.utils.handPointerHover
import com.jawharat.manifest.utils.string

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDriverDialog(
    driverToEdit: Driver?,
    isEdit: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (driver: Driver?, isEdit: Boolean) -> Unit
) {
    var driverName by remember { mutableStateOf(driverToEdit?.name.orEmpty()) }
    val phoneNumber = rememberTextFieldState(initialText = driverToEdit?.phone.orEmpty())
    val destination = rememberTextFieldState(initialText = driverToEdit?.destination.orEmpty())
    val driverId = rememberTextFieldState(initialText = driverToEdit?.driverId.orEmpty())

    val isConfirmEnabled by rememberUpdatedState(
        driverName.length > 3 &&
                phoneNumber.text.length > 4 &&
                destination.text.length >= 2 &&
                driverId.text.length > 3
    )

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
                    text = if (isEdit) Res.string.edit_driver_details.string else Res.string.add_new_driver.string,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )

                AppTextField(
                    state = driverId,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = Res.string.driver_id_number.string
                )

                AppTextField(
                    value = driverName,
                    onValueChange = { driverName = it },
                    placeholder = Res.string.driver.string,
                )

                AppTextField(
                    state = phoneNumber,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = Res.string.driver_phone_number.string
                )

                AppTextField(
                    state = destination,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = Res.string.destination.string
                )

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
                                    driverToEdit?.copy(
                                        name = driverName,
                                        phone = phoneNumber.text.toString(),
                                        destination = destination.text.toString(),
                                    ),
                                    true
                                )
                            else
                                onConfirm(
                                    Driver(
                                        id = "",
                                        name = driverName,
                                        phone = phoneNumber.text.toString(),
                                        destination = destination.text.toString(),
                                        driverId = driverToEdit?.driverId.orEmpty()
                                    ),
                                    false
                                )
                        },
                        enabled = isConfirmEnabled,
                        modifier = Modifier.handPointerHover()
                    ) {
                        Text(text = if (isEdit) Res.string.save_changes.string else Res.string.confirm.string)
                    }
                }
            }
        }
    }
}
