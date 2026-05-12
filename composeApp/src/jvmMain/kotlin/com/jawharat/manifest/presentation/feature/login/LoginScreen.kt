package com.jawharat.manifest.presentation.feature.login

import ManifestDesktop.composeApp.BuildConfig
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jawharat.manifest.presentation.components.AppTextField
import com.jawharat.manifest.presentation.feature.shared.AppSnackBarVisuals
import com.jawharat.manifest.presentation.feature.shared.LocalSnackBarState
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.ic_jawharat
import com.jawharat.manifest.resources.ic_lock
import com.jawharat.manifest.resources.ic_mail
import com.jawharat.manifest.resources.ic_password_invisible
import com.jawharat.manifest.resources.ic_password_visible
import com.jawharat.manifest.resources.login
import com.jawharat.manifest.resources.login_to_continue
import com.jawharat.manifest.resources.password
import com.jawharat.manifest.resources.username
import com.jawharat.manifest.utils.Listen
import com.jawharat.manifest.utils.handPointerHover
import com.jawharat.manifest.utils.moveFocusOnEnter
import com.jawharat.manifest.utils.painter
import com.jawharat.manifest.utils.string
import org.koin.ext.clearQuotes

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(viewModel: LoginViewModel, onNavigateToHome: () -> Unit) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(null)

    event?.Listen {
        when (it) {
            LoginUiEvent.OnNavigateToHome -> onNavigateToHome()
        }
    }

    Content(state = state, viewModel = viewModel)
}

@Composable
fun Content(state: LoginUiState, viewModel: LoginViewModel) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        snackbarHost = {
            SnackbarHost(LocalSnackBarState.current.nativeHostState) { data ->
                val visuals = data.visuals as? AppSnackBarVisuals
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .widthIn(max = 400.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (visuals?.isError == true)
                            MaterialTheme.colorScheme.errorContainer
                        else
                            Color(0xFF0C9912),
                        contentColor = if (visuals?.isError == true)
                            MaterialTheme.colorScheme.onErrorContainer
                        else
                            Color.White
                    )
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        visuals?.icon?.let { Icon(painter = it.painter, null) }
                        Text(data.visuals.message)
                    }
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoading)
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.zIndex(1f))
            }

            Column(
                modifier = Modifier
                    .widthIn(min = 350.dp, max = 450.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = Res.drawable.ic_jawharat.painter,
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )

                Text(
                    text = Res.string.login_to_continue.string,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                AppTextField(
                    state = state.emailState,
                    modifier = Modifier.fillMaxWidth()
                        .height(60.dp)
                        .moveFocusOnEnter(focusManager),
                    placeholder = Res.string.username.string,
                    leadingIcon = {
                        Icon(painter = Res.drawable.ic_mail.painter, null, tint = Color.Gray)
                    }
                )

                var isPasswordVisible by remember { mutableStateOf(false) }

                AppTextField(
                    state = state.passwordState,
                    placeholder = Res.string.password.string,
                    modifier = Modifier.fillMaxWidth()
                        .height(60.dp)
                        .onKeyEvent {
                            if (it.key == Key.Enter && state.isLoginEnabled) {
                                viewModel.login()
                                true
                            } else
                                false
                        },
                    outputTransformation = if (isPasswordVisible) null else PasswordOutputTransformation(),
                    leadingIcon = {
                        Icon(painter = Res.drawable.ic_lock.painter, null, tint = Color.Gray)
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { isPasswordVisible = !isPasswordVisible },
                            modifier = Modifier.handPointerHover()
                        ) {
                            Icon(
                                painter =
                                    if (isPasswordVisible) Res.drawable.ic_password_visible.painter
                                    else Res.drawable.ic_password_invisible.painter,
                                contentDescription = null
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    modifier = Modifier.fillMaxWidth()
                        .height(48.dp)
                        .handPointerHover(),
                    onClick = viewModel::login,
                    shape = RoundedCornerShape(8.dp),
                    enabled = state.isLoginEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE2B631),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = Res.string.login.string,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(Modifier.height(48.dp))

                Text(
                    text = "V${BuildConfig.APP_VERSION.clearQuotes()}(${BuildConfig.BUILD_NUMBER})",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Stable
class PasswordOutputTransformation(val mask: Char = '\u2022') : OutputTransformation {
    override fun TextFieldBuffer.transformOutput() {
        val currentLength = length

        replace(0, currentLength, mask.toString().repeat(currentLength))
    }
}
