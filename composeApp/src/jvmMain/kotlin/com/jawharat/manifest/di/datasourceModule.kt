package com.jawharat.manifest.di

import com.jawharat.manifest.data.local.datasource.AppLocalDataSource
import com.jawharat.manifest.data.local.datasource.AppLocalDataSourceImpl
import com.jawharat.manifest.data.remote.observer.AuthObserver
import com.jawharat.manifest.data.remote.proxy.AuthProxy
import com.jawharat.manifest.data.remote.proxy.AuthProxyImpl
import com.jawharat.manifest.data.remote.datasource.AppRemoteDataSource
import com.jawharat.manifest.data.remote.datasource.AppRemoteDataSourceImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataSourceModule = module {
    single<AppLocalDataSource> { AppLocalDataSourceImpl() }
    single<AppRemoteDataSource> {
        AppRemoteDataSourceImpl(
            manifestApiService = get(),
            pdfHttpClient = get(named("pdfClient")),
            mistralHttpClient = get(named("mistralClient")),
        )
    }
    single<AuthProxy> { AuthProxyImpl(get(), get()) }

    single { AuthObserver() }
}
