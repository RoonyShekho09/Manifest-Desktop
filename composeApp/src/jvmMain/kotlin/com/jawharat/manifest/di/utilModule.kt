package com.jawharat.manifest.di

import com.github.javakeyring.Keyring
import com.jawharat.manifest.presentation.feature.home.scanner.DocumentScanner
import com.jawharat.manifest.presentation.feature.home.scanner.IDocumentScanner
import com.jawharat.manifest.utils.NoOpKeyring
import org.koin.dsl.module

val utilModule = module {
    runCatching { Keyring.create() }.getOrDefault(NoOpKeyring())
    single<IDocumentScanner> { DocumentScanner() }
}
