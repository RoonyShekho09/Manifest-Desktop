@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.presentation.feature.vehicles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.presentation.components.AppTextField
import com.example.myapplication.presentation.components.EditCarDialog
import com.example.myapplication.utils.painter
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.ic_edit
import com.jawharat.manifest.resources.ic_qr_code_scanning

@Composable
fun CarsScreen(viewModel: VehiclesViewModel) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    Content(state = state, viewModel = viewModel)
}

@Composable
private fun Content(state: VehiclesUiState, viewModel: VehiclesViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cars Management") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(24.dp)
        ) {
            item {
                AppTextField(
                    state = state.searchState.query,
                    placeholder = "Search by ",
                    modifier = Modifier.width(400.dp)
                )
            }
            items(state.filteredVehicles) { car ->
                CarRow(
                    driver = car,
                    onEditClick = { viewModel.onEditClick(car.id) },
                    onQrCodeClick = { viewModel.onQrCodeClick(car.id) }
                )
            }
        }
    }

    if (state.isDialogVisible && state.vehicleToEdit != null)
        EditCarDialog(
            car = state.vehicleToEdit,
            onDismiss = viewModel::onDismissDialog,
            onSave = {}
        )
}

@Composable
fun CarRow(
    driver: VehicleUiState,
    onEditClick: () -> Unit,
    onQrCodeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
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
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Line: ${driver.line}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Vehicle: ${driver.carType} (${driver.type}) | Plate: ${driver.plateNumber}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "Price: $${driver.price}",
                    style = MaterialTheme.typography.bodyMedium,
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
                        style = MaterialTheme.typography.bodyMedium
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
