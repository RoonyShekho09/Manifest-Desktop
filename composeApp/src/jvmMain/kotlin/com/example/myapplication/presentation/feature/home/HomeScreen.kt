package com.example.myapplication.presentation.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.utils.Listen
import com.example.myapplication.utils.painter
import me.sample.library.resources.Res
import me.sample.library.resources.ic_qr_code_scanning

@Composable
fun HomeScreen(viewModel: HomeViewModel) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(null)

    event?.Listen {
        when (it) {
            else -> {}
        }
    }

    Content(state = state, viewModel = viewModel)
}

@Composable
fun Content(state: HomeUiState, viewModel: HomeViewModel) {
    Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Register Trip", style = MaterialTheme.typography.headlineMedium)

        Button(onClick = { }) {
            Icon(painter = Res.drawable.ic_qr_code_scanning.painter, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Scan QR Code")
        }

        Spacer(Modifier.height(16.dp))

        FormSection(title = "Trip Details") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("From") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("To") },
                    modifier = Modifier.weight(1f)
                )
            }
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Section: Passenger & Driver
        FormSection(title = "Personnel") {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Driver Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("ID Card") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Mobile Number") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        content()
    }
}
