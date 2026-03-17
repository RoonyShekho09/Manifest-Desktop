@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.myapplication

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.di.dataSourceModule
import com.example.myapplication.di.networkModule
import com.example.myapplication.di.repositoryModule
import com.example.myapplication.di.viewModelModule
import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.presentation.navigation.AppNavigation
import com.example.myapplication.presentation.navigation.Screen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.compose.koinInject
import org.koin.core.context.startKoin

@Composable
@Preview
fun App() {
    startKoin {
        modules(networkModule, viewModelModule, repositoryModule, dataSourceModule)
    }

    val repository = koinInject<AuthRepository>()

    MaterialTheme {
        AppNavigation(startDestination = if (repository.isUserLoggedIn) Screen.Home else Screen.Login)
    }
}
