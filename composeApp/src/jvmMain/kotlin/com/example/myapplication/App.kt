@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.myapplication

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.di.networkModule
import com.example.myapplication.di.viewModelModule
import com.example.myapplication.presentation.navigation.AppNavigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin

@Composable
@Preview
fun App() {
    startKoin {
        modules(networkModule, viewModelModule)
    }

    Dispatchers.setMain(Dispatchers.Swing)

    MaterialTheme {
        AppNavigation()
    }
}
