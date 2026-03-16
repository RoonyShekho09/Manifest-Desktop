package com.example.myapplication.presentation.feature.login

import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.presentation.base.BaseViewModel
import com.example.myapplication.presentation.feature.shared.AppSnackBarHostState
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.login_failed
import org.jetbrains.compose.resources.getString

class LoginViewModel(
    private val repository: AuthRepository,
    private val snackBar: AppSnackBarHostState,
) : BaseViewModel<LoginUiState, LoginUiEvent>(LoginUiState()) {

    fun login() = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = {
            repository.login(
                email = state.value.emailState.text.trim().toString(),
                password = state.value.passwordState.text.trim().toString()
            )
        },
        onSuccess = { emitEvent(LoginUiEvent.OnNavigateToHome) },
        onError = {
            snackBar.showFailure(message = getString(Res.string.login_failed))
        },
        onCompleted = { updateState { copy(isLoading = true) } }
    )
}
