@file:OptIn(ExperimentalMaterial3Api::class)

package com.jawharat.manifest.presentation.feature.vehicles

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jawharat.manifest.presentation.components.AddEditDispatchDialog
import com.jawharat.manifest.presentation.components.AddItemButton
import com.jawharat.manifest.presentation.components.AppTextField
import com.jawharat.manifest.presentation.components.ScrollToTopBox
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.cars_management
import com.jawharat.manifest.resources.edit
import com.jawharat.manifest.resources.generate_qr
import com.jawharat.manifest.resources.ic_edit
import com.jawharat.manifest.resources.ic_qr_code_scanning
import com.jawharat.manifest.resources.ic_refresh
import com.jawharat.manifest.resources.inside
import com.jawharat.manifest.resources.line_with_value
import com.jawharat.manifest.resources.outside
import com.jawharat.manifest.resources.price_iqd
import com.jawharat.manifest.resources.search_driver_placeholder
import com.jawharat.manifest.resources.vehicle_info
import com.jawharat.manifest.utils.painter
import com.jawharat.manifest.utils.string
import kotlinx.coroutines.launch

@Composable
fun CarsScreen(viewModel: DispatchesViewModel) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    Content(state = state, viewModel = viewModel)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Content(state: DispatchesUiState, viewModel: DispatchesViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Res.string.cars_management.string) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(
                        onClick = viewModel::onRefresh,
                        shape = CircleShape,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(painter = Res.drawable.ic_refresh.painter, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        val listState = rememberLazyListState()

        val filteredVehicles = state.dispatchSearchState.searchResults
        val query = state.dispatchSearchState.query

        if (state.isLoading)
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(24.dp)
            ) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppTextField(
                            state = query,
                            placeholder = Res.string.search_driver_placeholder.string,
                            modifier = Modifier.width(400.dp)
                        )

                        AddItemButton(onClick = viewModel::onAddClick)
                    }
                }
                items(filteredVehicles, key = { it.id }) { car ->
                    CarRow(
                        dispatch = car,
                        onEditClick = viewModel::onEditClick,
                        onGenerateQrCodeClick = viewModel::onGenerateQrCodeClick
                    )
                }
            }
            VerticalScrollbar(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 4.dp, top = 32.dp)
                    .fillMaxHeight(),
                adapter = rememberScrollbarAdapter(listState),
                style = ScrollbarStyle(
                    minimalHeight = 32.dp,
                    thickness = 8.dp,
                    shape = RoundedCornerShape(4.dp),
                    hoverDurationMillis = 300,
                    unhoverColor = Color.Black.copy(alpha = 0.12f),
                    hoverColor = Color.Black.copy(alpha = 0.50f)
                )
            )
        }

        val scope = rememberCoroutineScope()
        if (listState.firstVisibleItemIndex > 6)
            ScrollToTopBox {
                scope.launch {
                    listState.animateScrollToItem(0)
                }
            }
    }

    if (state.isDialogVisible)
        AddEditDispatchDialog(
            driverSearchState = state.driverSearchState,
            routes = state.price,
            vehicleTypes = state.carTypes,
            dispatchToEdit = state.dispatchToEdit,
            dispatchLines = state.dispatchLines,
            onDismiss = viewModel::onDismissDialog,
            onConfirm = viewModel::onConfirmAddEditDispatch,
        )
}

@Composable
private fun CarRow(
    dispatch: DispatchUiState,
    onEditClick: (String) -> Unit,
    onGenerateQrCodeClick: (String) -> Unit,
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
                    text = dispatch.driver.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = Res.string.line_with_value.string(dispatch.line.name),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = Res.string.vehicle_info.string(
                        dispatch.vehicleName,
                        dispatch.vehicleType,
                        dispatch.plateNumber
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                val price = dispatch.price
                val formattedPrice = if (price.length == 5) {
                    "${price.take(2)},${price.drop(2)}"
                } else {
                    price
                }

                Text(
                    text = Res.string.price_iqd.string(formattedPrice),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val statusColor =
                    if (dispatch.status == DriverStatus.INSIDE) Color(0xFF4CAF50) else Color(
                        0xFFF44336
                    )
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (dispatch.status == DriverStatus.INSIDE) Res.string.inside.string else Res.string.outside.string,
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
                    onClick = { onEditClick(dispatch.id) },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        painter = Res.drawable.ic_edit.painter,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(Res.string.edit.string)
                }
            }

            OutlinedButton(
                onClick = { onGenerateQrCodeClick(dispatch.id) },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    painter = Res.drawable.ic_qr_code_scanning.painter,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(Res.string.generate_qr.string)
            }
        }
    }
}
