package com.example.myapplication.data.remote.repository

import com.example.myapplication.data.remote.datasource.AppRemoteDataSource
import com.example.myapplication.data.remote.mapper.toDomain
import com.example.myapplication.domain.entity.Driver
import com.example.myapplication.domain.entity.Vehicle
import com.example.myapplication.domain.repository.ManifestRepository

class ManifestRepositoryImpl(private val dataSource: AppRemoteDataSource) : ManifestRepository {

    override suspend fun getDrivers(): List<Driver> = dataSource.getDrivers().toDomain()

    override suspend fun getVehicles(): List<Vehicle> = dataSource.getVehicles().toDomain()
}