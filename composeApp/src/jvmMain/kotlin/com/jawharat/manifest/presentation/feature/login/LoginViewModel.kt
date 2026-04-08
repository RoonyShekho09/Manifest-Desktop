package com.jawharat.manifest.presentation.feature.login

import androidx.compose.foundation.text.input.TextFieldState
import com.jawharat.manifest.domain.repository.AuthRepository
import com.jawharat.manifest.presentation.base.BaseViewModel
import com.jawharat.manifest.presentation.feature.shared.AppSnackBarHostState
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.login_failed
import com.jawharat.manifest.utils.IKeyringProvider
import org.jetbrains.compose.resources.getString


class LoginViewModel(
    private val repository: AuthRepository,
    private val snackBar: AppSnackBarHostState,
    private val keyring: IKeyringProvider
) : BaseViewModel<LoginUiState, LoginUiEvent>(LoginUiState()) {

    init {
        initializeSavedCredentials()
    }

    private fun initializeSavedCredentials() {
        runCatching {
            keyring.use { keyring ->
                val secret =
                    keyring.getKeyring()?.getPassword("jawharat-erbil", repository.lastUsedEmail)
                updateState {
                    copy(
                        emailState = TextFieldState(initialText = repository.lastUsedEmail),
                        passwordState = TextFieldState(initialText = secret.orEmpty())
                    )
                }
            }
        }
    }

    fun login() = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = {
            repository.login(
                email = state.value.emailState.text.trim().toString(),
                password = state.value.passwordState.text.trim().toString()
            )
        },
        onSuccess = {
            keyring.getKeyring().use { keyring ->
                keyring?.setPassword(
                    "jawharat-erbil",
                    state.value.emailState.text.toString(),
                    state.value.passwordState.text.toString()
                )
            }
            emitEvent(LoginUiEvent.OnNavigateToHome)
        },
        onError = {
            snackBar.showFailure(message = getString(Res.string.login_failed))
        },
        onCompleted = { updateState { copy(isLoading = false) } }
    )
}
