package com.jawharat.manifest.data.remote.repository

import com.jawharat.manifest.data.local.datasource.AppLocalDataSource
import com.jawharat.manifest.data.remote.datasource.AppRemoteDataSource
import com.jawharat.manifest.data.remote.mapper.toDomain
import com.jawharat.manifest.data.remote.mapper.toEntity
import com.jawharat.manifest.data.remote.model.Passenger
import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.domain.entity.Vehicle
import com.jawharat.manifest.domain.repository.ManifestRepository

class ManifestRepositoryImpl(
    private val remoteDataSource: AppRemoteDataSource,
    private val localDataSource: AppLocalDataSource
) : ManifestRepository {

    override suspend fun getDrivers(fetch: Boolean): List<Driver> =
        if (!fetch && localDataSource.hasDriversInDb)
            localDataSource.queryDrivers().toDomain()
        else
            remoteDataSource.getDrivers().toDomain().also {
                localDataSource.insertDrivers(it.toEntity())
            }

    override suspend fun getVehicles(fetch: Boolean): List<Vehicle> =
        if (!fetch && localDataSource.hasVehiclesInDb)
            localDataSource.queryVehicles().toDomain()
        else
            remoteDataSource.getVehicles().toDomain().also {
                localDataSource.insertVehicles(it.toEntity())
            }

    override suspend fun submitManifest(
        driverName: String,
        vehicleNumber: String,
        vehicleType: String,
        phoneNumber: String,
        to: String,
        price: String,
        passengers: List<Passenger>,
        driverId: String,
    ) = remoteDataSource.submitManifest(
        driverName = driverName,
        vehicleNumber = vehicleNumber,
        vehicleType = vehicleType,
        phoneNumber = phoneNumber,
        to = to,
        price = price,
        passengers = passengers,
        driverId = driverId
    )

    override suspend fun scanManifestQrCode(id: String) {
        remoteDataSource.scanManifestQrCode(id)
    }

    override suspend fun scanDriverQrCode(id: String) =
        remoteDataSource.scanDriverQrCode(id).toDomain()

    override suspend fun scanVehicleQrCode(id: String) =
        remoteDataSource.scanVehicleQrCode(id).toDomain()
}
