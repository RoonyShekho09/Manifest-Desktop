@file:OptIn(ExperimentalCoroutinesApi::class)

package com.jawharat.manifest.presentation.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jawharat.manifest.presentation.components.AppTextField
import com.jawharat.manifest.presentation.feature.home.components.dialog.AddPassengerDialog
import com.jawharat.manifest.presentation.feature.home.components.dialog.LogoutConfirmationDialog
import com.jawharat.manifest.presentation.feature.shared.AppSnackBarVisuals
import com.jawharat.manifest.presentation.feature.shared.LocalSnackBarState
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.date
import com.jawharat.manifest.resources.driver_id_number
import com.jawharat.manifest.resources.driver_name
import com.jawharat.manifest.resources.driver_phone_number
import com.jawharat.manifest.resources.from_to
import com.jawharat.manifest.resources.ic_qr_code_scanning
import com.jawharat.manifest.resources.logout
import com.jawharat.manifest.resources.passengers
import com.jawharat.manifest.resources.personnel
import com.jawharat.manifest.resources.price
import com.jawharat.manifest.resources.register_trip
import com.jawharat.manifest.resources.scan_qr_code
import com.jawharat.manifest.resources.trip_details
import com.jawharat.manifest.resources.vehicle_information
import com.jawharat.manifest.resources.vehicle_number
import com.jawharat.manifest.resources.vehicle_type
import com.jawharat.manifest.utils.Listen
import com.jawharat.manifest.utils.painter
import com.jawharat.manifest.utils.string
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel(), onLogout: () -> Unit) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(null)

    event?.Listen {
        when (it) {
            HomeUiEvent.OnLogout -> onLogout()
        }
    }

    Content(state = state, viewModel = viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(state: HomeUiState, viewModel: HomeViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = Res.string.register_trip.string,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                actions = {
                    Button(
                        onClick = viewModel::onLogoutClick,
                        modifier = Modifier.padding(end = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary)
                    ) {
                        Text(text = Res.string.logout.string, color = Color(0xC1A52B2B))
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(LocalSnackBarState.current.nativeHostState) { data ->
                val visuals = data.visuals as? AppSnackBarVisuals
                Card(
                    modifier = Modifier.padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            if (visuals?.isError == true)
                                MaterialTheme.colorScheme.errorContainer
                            else
                                Color(0xFF0C9912),
                        contentColor = if (visuals?.isError == true) MaterialTheme.colorScheme.onErrorContainer else Color.White
                    )
                ) {
                    Row(
                        Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        visuals?.icon?.let {
                            Icon(
                                painter = it.painter,
                                contentDescription = null
                            )
                        }
                        Text(data.visuals.message)
                    }
                }
            }
        }
    ) { paddingValues ->

        var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            QrCodeScanner(
                onResult = viewModel::onQrCodeResult,
                onFrame = { imageBitmap = it },
            )

//                imageBitmap?.let {
//                    Image(
//                        bitmap = it,
//                        contentDescription = null,
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier.fillMaxSize()
//                    )
//                }
//
//                ScanGuideOverlay()

            Column(
                modifier = Modifier.padding(24.dp).padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FormSection(title = Res.string.trip_details.string) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        AppTextField(
                            value = state.manifest.date,
                            placeholder = Res.string.date.string,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )
                        AppTextField(
                            value = state.manifest.price,
                            placeholder = Res.string.price.string,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    AppTextField(
                        value = if (state.manifest.from.isNotEmpty()) "${state.manifest.from} - ${state.manifest.to}" else "",
                        placeholder = Res.string.from_to.string,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                FormSection(title = Res.string.vehicle_information.string) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        AppTextField(
                            value = state.manifest.vehicleNumber,
                            placeholder = Res.string.vehicle_number.string,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )

                        AppTextField(
                            value = state.manifest.vehicleType,
                            placeholder = Res.string.vehicle_type.string,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            modifier = Modifier.weight(1f)
                                .clickable(onClick = viewModel::onPassengerFieldClick)
                        ) {
                            AppTextField(
                                value = state.manifest.passengers.ifEmpty { "" }.toString(),
                                placeholder = Res.string.passengers.string,
                                onValueChange = {},
                                readOnly = false,
                                enabled = false
                            )
                            Box(modifier = Modifier.matchParentSize())
                        }
                    }
                }

                FormSection(title = Res.string.personnel.string) {
                    AppTextField(
                        value = state.manifest.driverName,
                        placeholder = Res.string.driver_name.string,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        AppTextField(
                            value = state.manifest.driverIdNumber,
                            placeholder = Res.string.driver_id_number.string,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )
                        AppTextField(
                            value = state.manifest.driverPhoneNumber,
                            placeholder = Res.string.driver_phone_number.string,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                FormSection(title = "") {
                    Button(
                        onClick = viewModel::onStartScanning,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            painter = Res.drawable.ic_qr_code_scanning.painter,
                            tint = Color.White,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = Res.string.scan_qr_code.string)
                    }
                }
            }
        }
    }

    if (state.isAddPassengersDialogVisible)
        AddPassengerDialog(onDismiss = viewModel::onDismissAddPassengerDialog)

    if (state.isLogoutConfirmationVisible)
        LogoutConfirmationDialog(
            onDismissLogoutConfirmation = viewModel::onDismissLogoutConfirmation,
            onLogout = viewModel::logout
        )
}

@Composable
fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = DividerDefaults.color.copy(0.6f)
        )
        content()
    }
}
