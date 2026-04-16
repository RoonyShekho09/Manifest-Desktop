@file:OptIn(ExperimentalCoroutinesApi::class)

package com.jawharat.manifest

import ManifestDesktop.composeApp.BuildConfig
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jawharat.manifest.di.dataSourceModule
import com.jawharat.manifest.di.databaseModule
import com.jawharat.manifest.di.networkModule
import com.jawharat.manifest.di.repositoryModule
import com.jawharat.manifest.di.utilModule
import com.jawharat.manifest.di.viewModelModule
import com.jawharat.manifest.domain.repository.AuthRepository
import com.jawharat.manifest.presentation.navigation.AppNavigation
import com.jawharat.manifest.presentation.navigation.Screen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.compose.koinInject
import org.koin.core.context.startKoin
import io.sentry.kotlin.multiplatform.Sentry
import kotlinx.coroutines.runBlocking

@Composable
@Preview
fun App() {
    startKoin {
        modules(
            networkModule,
            viewModelModule,
            repositoryModule,
            dataSourceModule,
            databaseModule,
            utilModule
        )
    }

    Sentry.init { options ->
        options.dsn =
            "https://c540b9e616e448c84ff4d2e200429d5b@o4511178272997376.ingest.de.sentry.io/4511178413834320"
        options.debug = false
        options.sendDefaultPii = true
    }

    val repository = koinInject<AuthRepository>()

    if (isNewUpdateAvailable(repository)) {
        Dialog(
            onDismissRequest = {},
            properties = remember { DialogProperties() },
            content = {

            }
        )
        return
    }

    MaterialTheme {
        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl
        ) {
            AppNavigation(startDestination = if (repository.isUserLoggedIn && !repository.hasSessionExpired) Screen.Home else Screen.Login)
        }
    }
}

private fun isNewUpdateAvailable(repository: AuthRepository): Boolean {
    val current = BuildConfig.BUILD_NUMBER
    var isAvailable = false
    runBlocking {
        isAvailable = repository.isUpdateAvailable(
            currentVersion = current.toString(),
            versionFileUrl = "https://github.com/RoonyShekho09/Manifest-Desktop-Releases/blob/main/buildNumber.txt"
        )
    }

    return isAvailable
}
