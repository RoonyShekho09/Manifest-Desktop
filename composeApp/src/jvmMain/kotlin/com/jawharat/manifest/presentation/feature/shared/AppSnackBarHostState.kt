package com.jawharat.manifest.presentation.feature.shared

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.compositionLocalOf
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.ic_check
import com.jawharat.manifest.resources.ic_warning
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

class AppSnackBarHostState(val nativeHostState: SnackbarHostState) {

    suspend fun showSuccess(message: String) {
        val visuals =
            AppSnackBarVisuals(message = message, icon = Res.drawable.ic_check, isError = false)
        nativeHostState.showSnackbar(visuals)
    }

    suspend fun showFailure(message: StringResource) {
        val visuals =
            AppSnackBarVisuals(
                message = getString(message),
                icon = Res.drawable.ic_warning,
                isError = true
            )
        nativeHostState.showSnackbar(visuals)
    }
}

class AppSnackBarVisuals(
    override val message: String,
    val icon: DrawableResource,
    val isError: Boolean,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short
) : SnackbarVisuals

private val snackBarState = AppSnackBarHostState(nativeHostState = SnackbarHostState())

val LocalSnackBarState = compositionLocalOf { snackBarState }