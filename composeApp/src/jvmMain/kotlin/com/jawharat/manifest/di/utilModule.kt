package com.jawharat.manifest.di

import com.github.javakeyring.Keyring
import com.jawharat.manifest.presentation.feature.home.scanner.DocumentScanner
import com.jawharat.manifest.presentation.feature.home.scanner.IDocumentScanner
import com.jawharat.manifest.utils.IKeyringProvider
import com.jawharat.manifest.utils.KeyringProvider
import com.jawharat.manifest.utils.NoOpKeyringProvider
import org.koin.dsl.module

val utilModule = module {
    single<IKeyringProvider> {
        runCatching { KeyringProvider(Keyring.create()) }
            .getOrDefault(NoOpKeyringProvider())
    }

    single<IDocumentScanner> { DocumentScanner() }
}
