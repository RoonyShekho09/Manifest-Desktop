package com.example.myapplication.di

import com.example.myapplication.presentation.feature.home.HomeViewModel
import com.example.myapplication.presentation.feature.login.LoginViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::LoginViewModel)
}
