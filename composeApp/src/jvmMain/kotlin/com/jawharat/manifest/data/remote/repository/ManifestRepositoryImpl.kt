package com.jawharat.manifest.data.remote.repository

import com.jawharat.manifest.data.local.datasource.AppLocalDataSource
import com.jawharat.manifest.data.remote.datasource.AppRemoteDataSource
import com.jawharat.manifest.data.remote.mapper.toDomain
import com.jawharat.manifest.data.remote.mapper.toEntity
import com.jawharat.manifest.data.remote.model.LineResponse
import com.jawharat.manifest.data.remote.model.Passenger
import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.domain.entity.Line
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

    override suspend fun addDriver(
        driverId: String?,
        name: String?,
        phoneNumber: String?,
        destination: String?
    ) = remoteDataSource.addDriver(
        driverId = driverId,
        name = name,
        phoneNumber = phoneNumber,
        destination = destination
    )

    override suspend fun addVehicle(
        vehicleNumber: String?,
        type: String?,
        carType: String?,
        price: Int?,
        driverId: String?,
        line: String?
    ) = remoteDataSource.addVehicle(
        vehicleNumber = vehicleNumber,
        type = type,
        carType = carType,
        price = price,
        driverId = driverId,
        line = line
    )

    override suspend fun editDriver(
        driverId: String?,
        name: String?,
        phoneNumber: String?,
        destination: String?,
        id: String
    ) = remoteDataSource.editDriver(
        driverId = driverId,
        name = name,
        phoneNumber = phoneNumber,
        destination = destination,
        id = id
    )

    override suspend fun editVehicle(
        vehicleNumber: String?,
        type: String?,
        carType: String?,
        price: Int?,
        driverId: String?,
        line: String?,
        id: String
    ) = remoteDataSource.editVehicle(
        vehicleNumber = vehicleNumber,
        type = type,
        carType = carType,
        price = price,
        driverId = driverId,
        line = line,
        id = id
    )

    override suspend fun getLines() =
        if (localDataSource.hasLinesInDb)
            localDataSource.queryLines().toDomain()
        else
            remoteDataSource.getLines()
                .also { localDataSource.insertLines(it.toEntity()) }
                .toDomain()

    override suspend fun scanManifestQrCode(id: String) {
        remoteDataSource.scanManifestQrCode(id)
    }

    override suspend fun scanDriverQrCode(id: String) =
        remoteDataSource.scanDriverQrCode(id).toDomain()

    override suspend fun scanVehicleQrCode(id: String) =
        remoteDataSource.scanVehicleQrCode(id).toDomain()
}
