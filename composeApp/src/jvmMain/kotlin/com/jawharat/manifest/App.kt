@file:OptIn(ExperimentalCoroutinesApi::class)

package com.jawharat.manifest

import ManifestDesktop.composeApp.BuildConfig
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jawharat.manifest.domain.entity.UpdateInfo
import com.jawharat.manifest.domain.repository.AuthRepository
import com.jawharat.manifest.presentation.navigation.AppNavigation
import com.jawharat.manifest.presentation.navigation.Screen
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.dismiss
import com.jawharat.manifest.resources.download
import com.jawharat.manifest.resources.mandatory_update_msg
import com.jawharat.manifest.resources.mandatory_update_title
import com.jawharat.manifest.resources.optional_update_msg
import com.jawharat.manifest.resources.update_available_title
import com.jawharat.manifest.utils.string
import io.sentry.Sentry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.compose.koinInject
import java.awt.Desktop
import java.net.URI

@Composable
@Preview
fun App() {
    Sentry.init { options ->
        with(options) {
            dsn = "https://bcc0bf3b1535462692fcd23a81ca0a49@app.glitchtip.com/22427"
            tracesSampleRate = 0.01 // 1% of transactions
            isDebug = false
            isSendDefaultPii = true
        }
    }

    val repository = koinInject<AuthRepository>()
    var updateInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    var isUpdateDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        updateInfo = checkUpdates(repository)
    }

    LaunchedEffect(updateInfo) {
        updateInfo?.let {
            isUpdateDialogVisible = it.build > CURRENT_BUILD_NUMBER
        }
    }

    AnimatedVisibility(isUpdateDialogVisible) {
        updateInfo?.let {
            UpdateDialog(
                isForced = it.isForced || it.minBuild > CURRENT_BUILD_NUMBER,
                onDismiss = { isUpdateDialogVisible = false }
            )
        }
    }

    if (isUpdateDialogVisible && updateInfo?.isForced == true) return

    MaterialTheme {
        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl
        ) {
            AppNavigation(startDestination = if (repository.isUserLoggedIn && !repository.hasSessionExpired) Screen.Home else Screen.Login)
        }
    }
}

@Composable
private fun UpdateDialog(isForced: Boolean, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = {
            if (!isForced)
                onDismiss()
        },
        properties = remember { DialogProperties() },
        content = {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.elevatedCardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isForced) {
                            Res.string.mandatory_update_title.string
                        } else {
                            Res.string.update_available_title.string
                        },
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isForced) {
                            Res.string.mandatory_update_msg.string
                        } else {
                            Res.string.optional_update_msg.string
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (!isForced) {
                            OutlinedButton(onClick = onDismiss) {
                                Text(text = Res.string.dismiss.string)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Button(
                            onClick = {
                                val downloadUrl =
                                    "https://github.com/RoonyShekho09/Manifest-Desktop-Releases/releases/latest/download/manifest.msi"
                                if (Desktop.isDesktopSupported() && Desktop.getDesktop()
                                        .isSupported(Desktop.Action.BROWSE)
                                ) {
                                    Desktop.getDesktop().browse(URI(downloadUrl))
                                }
                            }
                        ) {
                            Text(text = Res.string.download.string)
                        }
                    }
                }
            }
        }
    )
}

private suspend fun checkUpdates(repository: AuthRepository): UpdateInfo {
    val current = BuildConfig.BUILD_NUMBER

    return repository.isUpdateAvailable(
        currentVersion = current.toString(),
        versionFileUrl = "https://raw.githubusercontent.com/RoonyShekho09/Manifest-Desktop-Releases/refs/heads/main/buildNumber.txt"
    )
}


const val CURRENT_BUILD_NUMBER = BuildConfig.BUILD_NUMBER