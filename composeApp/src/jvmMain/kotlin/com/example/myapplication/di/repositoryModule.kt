package com.example.myapplication.di

import com.example.myapplication.data.remote.repository.AuthRepositoryImpl
import com.example.myapplication.data.remote.repository.ManifestRepositoryImpl
import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.domain.repository.ManifestRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<ManifestRepository> { ManifestRepositoryImpl(get()) }
}
