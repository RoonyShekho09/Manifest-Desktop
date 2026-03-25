package com.jawharat.manifest.di

import com.jawharat.manifest.data.local.datasource.AppLocalDataSource
import com.jawharat.manifest.data.local.datasource.AppLocalDataSourceImpl
import com.jawharat.manifest.data.remote.datasource.AppRemoteDataSource
import com.jawharat.manifest.data.remote.datasource.AppRemoteDataSourceImpl
import org.koin.dsl.module

val dataSourceModule = module {
    single<AppLocalDataSource> { AppLocalDataSourceImpl(database = get()) }
    single<AppRemoteDataSource> { AppRemoteDataSourceImpl(get()) }
}
