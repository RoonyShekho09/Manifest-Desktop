package com.jawharat.manifest.presentation.feature.home.components.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.already_submitted_msg
import com.jawharat.manifest.resources.closing_in
import com.jawharat.manifest.resources.countdown_title
import com.jawharat.manifest.resources.dismiss
import com.jawharat.manifest.utils.string
import kotlinx.coroutines.delay

@Composable
fun CountdownDialog(
    initialTime: Int,
    onDismissRequest: () -> Unit
) {
    var secondsLeft by remember { mutableStateOf(initialTime) }
    val duration by remember {
        derivedStateOf {
            val minutes = secondsLeft / 60
            val seconds = secondsLeft % 60
            "%02d:%02d".format(minutes, seconds)
        }
    }

    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000L)
            secondsLeft--
        }
        onDismissRequest()
    }

    DialogWindow(
        title = Res.string.countdown_title.string,
        resizable = false,
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
                    text = Res.string.already_submitted_msg.string,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = Res.string.closing_in.string(duration),
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = onDismissRequest) {
                    Text(text = Res.string.dismiss.string)
                }
            }
        }
    }
}
