package com.jawharat.manifest.di

import com.github.javakeyring.Keyring
import org.koin.dsl.module

val utilModule = module {
    single { Keyring.create() }
}
