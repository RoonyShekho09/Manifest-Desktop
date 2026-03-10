package com.example.myapplication.presentation.feature.login

import com.example.myapplication.presentation.base.BaseViewModel
import com.example.myapplication.presentation.feature.shared.AppSnackBarHostState
import me.sample.library.resources.Res
import me.sample.library.resources.login_failed
import org.jetbrains.compose.resources.getString

class LoginViewModel(
    private val snackBar: AppSnackBarHostState,
) : BaseViewModel<LoginUiState, LoginUiEvent>(LoginUiState()) {

    fun login() = tryToExecute(
        block = {},
        onSuccess = {},
        onError = {
            snackBar.showFailure(message = getString(Res.string.login_failed))
        }
    )
}
