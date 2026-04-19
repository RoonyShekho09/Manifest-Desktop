package com.jawharat.manifest.presentation.feature.home.components.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay

@Composable
fun CountdownDialog(
    initialTime: Int = 10,
    onDismissRequest: () -> Unit
) {
    var timeLeft by remember { mutableStateOf(initialTime) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
        onDismissRequest()
    }

    DialogWindow(
        title = "Countdown",
        onCloseRequest = onDismissRequest
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Closing in $timeLeft seconds",
                    style = MaterialTheme.typography.h6
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onDismissRequest) {
                    Text("Dismiss")
                }
            }
        }
    }
}

fun main() = application {
    var isDialogOpen by remember { mutableStateOf(true) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Main Application"
    ) {
        MaterialTheme {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = { isDialogOpen = true }) {
                    Text("Open Dialog")
                }
            }

            if (isDialogOpen) {
                CountdownDialog(
                    initialTime = 5,
                    onDismissRequest = { isDialogOpen = false }
                )
            }
        }
    }
}
