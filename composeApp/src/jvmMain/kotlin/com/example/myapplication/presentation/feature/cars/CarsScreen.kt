package com.example.myapplication.presentation.feature.cars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.presentation.components.EditCarDialog
import com.example.myapplication.utils.painter
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.ic_edit
import com.jawharat.manifest.resources.ic_qr_code_scanning

@Composable
fun CarsScreen(viewModel: CarsViewModel) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    Content(state = state, viewModel = viewModel)
}

@Composable
private fun Content(state: CarsUiState, viewModel: CarsViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cars Management") },
                elevation = 4.dp
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.cars) { car ->
                CarRow(
                    driver = car,
                    onEditClick = { viewModel.onEditClick(car.id) },
                    onQrCodeClick = { viewModel.onQrCodeClick(car.id) }
                )
            }
        }
    }

    if (state.isDialogVisible)
        EditCarDialog(
            car = state.cars.first(),
            onDismiss = viewModel::onDismissDialog,
            onSave = {}
        )
}

@Composable
fun CarRow(
    driver: Car,
    onEditClick: () -> Unit,
    onQrCodeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(2f)) {
                Text(
                    text = driver.driverName,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Line: ${driver.lineFrom} → ${driver.lineTo}",
                    style = MaterialTheme.typography.body1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Vehicle: ${driver.carType} (${driver.type}) | Plate: ${driver.plateNumber}",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
                Text(
                    text = "Price: $${driver.price}",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val statusColor =
                    if (driver.status == DriverStatus.INSIDE) Color(0xFF4CAF50) else Color(
                        0xFFF44336
                    )
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = driver.status.name,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.body2
                    )
                }
            }

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        painter = Res.drawable.ic_edit.painter,
                        contentDescription = "Edit Driver",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Edit")
                }
                Button(onClick = onQrCodeClick) {
                    Icon(
                        painter = Res.drawable.ic_qr_code_scanning.painter,
                        contentDescription = "Generate QR",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("QR Code")
                }
            }
        }
    }
}
