package com.example.myapplication.di

import com.example.myapplication.presentation.feature.cars.CarsViewModel
import com.example.myapplication.presentation.feature.drivers.DriversViewModel
import com.example.myapplication.presentation.feature.home.HomeViewModel
import com.example.myapplication.presentation.feature.login.LoginViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::DriversViewModel)
    viewModelOf(::CarsViewModel)
}