package com.jawharat.manifest

import ManifestDesktop.composeApp.BuildConfig
import com.jawharat.manifest.domain.repository.AuthRepository
import com.jawharat.manifest.presentation.base.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AppViewModel(
    private val repository: AuthRepository,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<AppUiState, Unit>(AppUiState(), ioDispatcher) {

    init {
        checkAppUpdates()
        updateState {
            copy(
                isUsedLoggedIn = repository.isUserLoggedIn,
                hasSessionExpired = repository.hasSessionExpired
            )
        }
    }

    private fun checkAppUpdates(currentBuild: Int = BuildConfig.BUILD_NUMBER) = tryToExecute(
        block = {
            repository.getUpdateInfo(
                versionFileUrl = "https://raw.githubusercontent.com/RoonyShekho09/Manifest-Desktop-Releases/refs/heads/main/buildNumber.txt"
            )
        },
        onSuccess = {
            updateState {
                copy(
                    isDialogVisible = it.latestBuild > currentBuild,
                    isUpdateAvailable = it.latestBuild > currentBuild,
                    isForcedUpdate = it.isForced || it.minBuild > currentBuild,
                )
            }
        }
    )

    fun onDismissDialog() = updateState { copy(isDialogVisible = false) }
}

data class AppUiState(
    val isDialogVisible: Boolean = false,
    val isForcedUpdate: Boolean = false,
    val isUsedLoggedIn: Boolean = false,
    val hasSessionExpired: Boolean = false,
    val isUpdateAvailable: Boolean = false,
)
