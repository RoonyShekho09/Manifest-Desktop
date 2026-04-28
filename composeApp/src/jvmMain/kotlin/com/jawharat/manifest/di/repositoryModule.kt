package com.jawharat.manifest.di

import com.jawharat.manifest.data.remote.repository.AuthRepositoryImpl
import com.jawharat.manifest.data.remote.repository.ManifestRepositoryImpl
import com.jawharat.manifest.domain.repository.AuthRepository
import com.jawharat.manifest.domain.repository.ManifestRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
    single<ManifestRepository> { ManifestRepositoryImpl(get(), get()) }
}
