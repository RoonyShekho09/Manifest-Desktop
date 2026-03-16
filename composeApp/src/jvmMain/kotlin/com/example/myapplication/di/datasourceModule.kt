package com.example.myapplication.di

import com.example.myapplication.data.local.datasource.AppLocalDataSource
import com.example.myapplication.data.local.datasource.AppLocalDataSourceImpl
import com.example.myapplication.data.remote.datasource.AppRemoteDataSource
import com.example.myapplication.data.remote.datasource.AppRemoteDataSourceImpl
import org.koin.dsl.module

val dataSourceModule = module {
    single<AppLocalDataSource> { AppLocalDataSourceImpl() }
    single<AppRemoteDataSource> { AppRemoteDataSourceImpl(get()) }
}
