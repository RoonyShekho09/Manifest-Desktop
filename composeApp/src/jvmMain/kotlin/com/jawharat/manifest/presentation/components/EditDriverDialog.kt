package com.jawharat.manifest.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jawharat.manifest.domain.entity.Driver

@Composable
fun EditDriverDialog(
    driver: Driver,
    onDismiss: () -> Unit,
    onSave: (Driver) -> Unit
) {
    val driverName = rememberTextFieldState(initialText = driver.name)
    val phoneNumber = rememberTextFieldState(initialText = driver.phone)
    val destination = rememberTextFieldState(initialText = driver.destination)

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

                AppTextField(driverName, modifier = Modifier.fillMaxWidth())

                AppTextField(phoneNumber, modifier = Modifier.fillMaxWidth())

                AppTextField(destination, modifier = Modifier.fillMaxWidth())

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
                                driver.copy(
                                    name = driverName.text.toString(),
                                    phone = phoneNumber.text.toString(),
                                    destination = destination.text.toString(),
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
