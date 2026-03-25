package com.jawharat.manifest.di

import com.jawharat.manifest.presentation.feature.vehicles.VehiclesViewModel
import com.jawharat.manifest.presentation.feature.drivers.DriversViewModel
import com.jawharat.manifest.presentation.feature.home.HomeViewModel
import com.jawharat.manifest.presentation.feature.login.LoginViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::DriversViewModel)
    viewModelOf(::VehiclesViewModel)
}