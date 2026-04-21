package com.jawharat.manifest.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    onConfirm: (Driver?) -> Unit
) {
    val driverNameState = rememberTextFieldState(initialText = driverToEdit?.name.orEmpty())
    val phoneNumberState = rememberTextFieldState(initialText = driverToEdit?.phone.orEmpty())
    val destinationState = rememberTextFieldState(initialText = driverToEdit?.destination.orEmpty())
    val driverIdState = rememberTextFieldState(initialText = driverToEdit?.driverId.orEmpty())

    val isConfirmEnabled by remember {
        derivedStateOf {
            driverNameState.text.length > 3 &&
                    phoneNumberState.text.length > 4 &&
                    destinationState.text.length >= 2 &&
                    driverIdState.text.length > 3
        }
    }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .widthIn(min = 400.dp, max = 600.dp)
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
                    state = driverIdState,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = Res.string.driver_id_number.string
                )

                AppTextField(
                    state = driverNameState,
                    placeholder = Res.string.driver.string,
                )

                AppTextField(
                    state = phoneNumberState,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = Res.string.driver_phone_number.string
                )

                AppTextField(
                    state = destinationState,
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
                                        name = driverNameState.text.toString(),
                                        phone = phoneNumberState.text.toString(),
                                        destination = destinationState.text.toString(),
                                        driverId = driverIdState.text.toString()
                                    )
                                )
                            else
                                onConfirm(
                                    Driver(
                                        id = "",
                                        name = driverNameState.text.toString(),
                                        phone = phoneNumberState.text.toString(),
                                        destination = destinationState.text.toString(),
                                        driverId = driverIdState.text.toString(),
                                        blocked = false
                                    )
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
