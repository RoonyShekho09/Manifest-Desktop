package com.jawharat.manifest.di

import com.jawharat.manifest.data.local.datasource.AppLocalDataSource
import com.jawharat.manifest.data.local.datasource.AppLocalDataSourceImpl
import com.jawharat.manifest.data.local.factory.SqliteEntityStoreFactory
import com.jawharat.manifest.data.remote.datasource.AppRemoteDataSource
import com.jawharat.manifest.data.remote.datasource.AppRemoteDataSourceImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataSourceModule = module {
    single { SqliteEntityStoreFactory(get()) }
    single<AppLocalDataSource> { AppLocalDataSourceImpl(database = get(), sqliteFactory = get()) }
    single<AppRemoteDataSource> { AppRemoteDataSourceImpl(get(), get(named("pdfClient"))) }
}
