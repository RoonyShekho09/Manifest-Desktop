package com.jawharat.manifest.presentation.feature.drivers

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
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
import com.jawharat.manifest.domain.entity.manifest.Driver
import com.jawharat.manifest.presentation.components.AddEditDriverDialog
import com.jawharat.manifest.presentation.components.AddItemButton
import com.jawharat.manifest.presentation.components.AppTextField
import com.jawharat.manifest.presentation.components.ScrollToTopBox
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.driver_id
import com.jawharat.manifest.resources.driver_management
import com.jawharat.manifest.resources.edit
import com.jawharat.manifest.resources.generate_qr
import com.jawharat.manifest.resources.ic_edit
import com.jawharat.manifest.resources.ic_location_on
import com.jawharat.manifest.resources.ic_profile
import com.jawharat.manifest.resources.ic_qr_code_scanning
import com.jawharat.manifest.resources.ic_refresh
import com.jawharat.manifest.resources.search_placeholder
import com.jawharat.manifest.utils.handPointerHover
import com.jawharat.manifest.utils.painter
import com.jawharat.manifest.utils.string
import kotlinx.coroutines.launch

@Composable
fun DriversScreen(viewModel: DriversViewModel) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    Content(state = state, viewModel = viewModel)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Content(state: DriverUiState, viewModel: DriversViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Res.string.driver_management.string) },
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
                            .handPointerHover()
                    ) {
                        Icon(painter = Res.drawable.ic_refresh.painter, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        val listState = rememberLazyListState()
        val filteredDrivers = state.mainSearchState.searchResults
        val query = state.mainSearchState.query

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
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    top = 24.dp,
                    bottom = 24.dp,
                    start = 24.dp,
                    end = 40.dp
                )
            ) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppTextField(
                            state = query,
                            placeholder = Res.string.search_placeholder.string,
                            modifier = Modifier.width(400.dp)
                        )
                        AddItemButton { viewModel.onEditClick("") }
                    }
                }
                items(filteredDrivers, key = { it.id }) { driver ->
                    DriverRow(
                        driver = driver,
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
        AddEditDriverDialog(
            driverToEdit = state.driverToEdit,
            isEdit = state.driverToEdit != null,
            onDismiss = viewModel::onDismissDialog,
            onConfirm = viewModel::onConfirmAddEditDriver,
        )
}

@Composable
fun DriverRow(
    driver: Driver,
    onEditClick: (String) -> Unit,
    onGenerateQrCodeClick: (String) -> Unit
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
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp),
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = driver.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Unspecified
                    )

                    if (driver.blocked) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = "Blocked",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                Text(
                    text = Res.string.driver_id.string(driver.driverId),
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
                    Text(text = driver.phoneNumber, style = MaterialTheme.typography.bodyMedium)
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

            OutlinedButton(
                onClick = { onEditClick(driver.id) },
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

            OutlinedButton(
                onClick = { onGenerateQrCodeClick(driver.id) },
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
