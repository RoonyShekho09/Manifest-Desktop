package com.jawharat.manifest.di

import com.github.javakeyring.Keyring
import com.jawharat.manifest.presentation.feature.home.IDocumentScanner
import com.jawharat.manifest.presentation.feature.home.DocumentScanner
import org.koin.dsl.module

val utilModule = module {
    single { Keyring.create() }
    single<IDocumentScanner> { DocumentScanner() }
}
