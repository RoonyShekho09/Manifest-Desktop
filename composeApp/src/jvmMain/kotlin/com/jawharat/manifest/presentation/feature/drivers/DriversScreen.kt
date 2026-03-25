package com.jawharat.manifest.presentation.feature.drivers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.presentation.components.AppTextField
import com.jawharat.manifest.presentation.components.EditDriverDialog
import com.jawharat.manifest.presentation.components.ScrollToTopBox
import com.jawharat.manifest.utils.painter
import com.jawharat.manifest.utils.string
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.driver_id
import com.jawharat.manifest.resources.driver_management
import com.jawharat.manifest.resources.edit
import com.jawharat.manifest.resources.ic_edit
import com.jawharat.manifest.resources.ic_location_on
import com.jawharat.manifest.resources.ic_profile
import com.jawharat.manifest.resources.ic_refresh
import com.jawharat.manifest.resources.search_placeholder
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
                    ) {
                        Icon(painter = Res.drawable.ic_refresh.painter, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        val listState = rememberLazyListState()
        val filteredDrivers = state.filteredDrivers
        val query = state.searchState.query

        if (state.isLoading)
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(24.dp)
        ) {
            item {
                AppTextField(
                    state = query,
                    placeholder = Res.string.search_placeholder.string,
                    modifier = Modifier.width(400.dp)
                )
            }
            items(filteredDrivers, key = { it.id }) { driver ->
                DriverRow(
                    driver = driver,
                    onEditClick = viewModel::onEditClick,
                )
            }
        }

        val scope = rememberCoroutineScope()
        if (listState.firstVisibleItemIndex > 6)
            ScrollToTopBox {
                scope.launch {
                    listState.animateScrollToItem(0)
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
                    text = Res.string.driver_id.string(driver.id),
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
        }
    }
}
