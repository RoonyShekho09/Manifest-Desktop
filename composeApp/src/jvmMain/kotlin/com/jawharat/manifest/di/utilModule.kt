package com.jawharat.manifest.di

import com.github.javakeyring.Keyring
import com.jawharat.manifest.presentation.feature.home.IPassportScanner
import com.jawharat.manifest.presentation.feature.home.PassportScanner
import org.koin.dsl.module

val utilModule = module {
    single { Keyring.create() }
    single<IPassportScanner> { PassportScanner() }
}
