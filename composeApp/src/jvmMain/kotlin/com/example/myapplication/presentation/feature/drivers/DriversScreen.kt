package com.example.myapplication.presentation.feature.drivers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.domain.entity.Driver
import com.example.myapplication.presentation.components.AppTextField
import com.example.myapplication.presentation.components.EditDriverDialog
import com.example.myapplication.utils.painter
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.ic_edit
import com.jawharat.manifest.resources.ic_location_on
import com.jawharat.manifest.resources.ic_profile

@Composable
fun DriversScreen(viewModel: DriversViewModel) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    Content(state = state, viewModel = viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(state: DriverUiState, viewModel: DriversViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Driver Management") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
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
                    placeholder = "Search..",
                    modifier = Modifier.width(400.dp)
                )
            }
            items(state.filteredDrivers) { driver ->
                DriverRow(
                    driver = driver,
                    onEditClick = viewModel::onEditClick,
                    onGenerateQrCodeClick = {}
                )
            }
        }
    }

    if (state.isDialogVisible && state.driverToEdit != null)
        EditDriverDialog(
            driver = state.driverToEdit,
            onDismiss = viewModel::onDismissDialog,
            onSave = {}
        )
}

@Composable
fun DriverRow(
    driver: Driver,
    onGenerateQrCodeClick: (String) -> Unit,
    onEditClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.LightGray)
            ) {
                Icon(
                    painter = Res.drawable.ic_profile.painter,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center).size(40.dp),
                    tint = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = driver.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ID: ${driver.id}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = Res.drawable.ic_profile.painter,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = driver.phone, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = Res.drawable.ic_location_on.painter,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = driver.destination, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Button(
                    onClick = { onEditClick(driver.id) },
                    modifier = Modifier.width(140.dp)
                ) {
                    Icon(
                        painter = Res.drawable.ic_edit.painter,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit")
                }

                OutlinedButton(
                    onClick = { onGenerateQrCodeClick(driver.id) },
                    modifier = Modifier.width(140.dp)
                ) {
                    Text("Generate QR")
                }
            }
        }
    }
}
