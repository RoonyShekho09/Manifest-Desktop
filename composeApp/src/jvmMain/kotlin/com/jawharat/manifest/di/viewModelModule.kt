package com.jawharat.manifest.di

import com.jawharat.manifest.presentation.feature.vehicles.DispatchesViewModel
import com.jawharat.manifest.presentation.feature.drivers.DriversViewModel
import com.jawharat.manifest.presentation.feature.home.HomeViewModel
import com.jawharat.manifest.presentation.feature.login.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModelOf(::LoginViewModel)
    viewModel { DriversViewModel(get(), get()) }
    viewModel { DispatchesViewModel(get()) }
}