package com.jawharat.manifest.data.remote.repository

import com.jawharat.manifest.data.local.datasource.AppLocalDataSource
import com.jawharat.manifest.data.remote.datasource.AppRemoteDataSource
import com.jawharat.manifest.data.remote.mapper.toDomain
import com.jawharat.manifest.data.remote.mapper.toEntity
import com.jawharat.manifest.data.remote.model.Passenger
import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.domain.entity.Dispatch
import com.jawharat.manifest.domain.entity.Line
import com.jawharat.manifest.domain.entity.VehicleType
import com.jawharat.manifest.domain.repository.ManifestRepository

class ManifestRepositoryImpl(
    private val remoteDataSource: AppRemoteDataSource,
    private val localDataSource: AppLocalDataSource
) : ManifestRepository {

    override suspend fun getDrivers(fetch: Boolean): List<Driver> =
        if (!fetch && localDataSource.drivers.hasRecords)
            localDataSource.drivers.query().toDomain()
        else
            remoteDataSource.getDrivers().toDomain().also {
                localDataSource.drivers.insert(it.toEntity())
            }

    override suspend fun getDispatches(fetch: Boolean): List<Dispatch> =
        if (!fetch && localDataSource.dispatches.hasRecords)
            localDataSource.dispatches.query().toDomain()
        else
            remoteDataSource.getDispatches().toDomain().also {
                localDataSource.dispatches.insert(it.toEntity())
            }

    override suspend fun submitManifest(
        driverName: String,
        vehicleNumber: String,
        vehicleType: String,
        phoneNumber: String,
        to: String,
        price: Int,
        passengers: List<Passenger>,
        driverId: String,
    ): ByteArray = remoteDataSource.submitManifest(
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
        plateNumber: String?,
        vehicleName: String?,
        vehicleType: String?,
        price: Int?,
        driverId: String?,
        line: String?
    ) = remoteDataSource.addVehicle(
        vehicleNumber = plateNumber,
        type = vehicleName,
        carType = vehicleType,
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
        vehicleName: String?,
        vehicleType: String?,
        price: Int?,
        driverId: String?,
        line: String?,
        id: String
    ) = remoteDataSource.editVehicle(
        vehicleNumber = vehicleNumber,
        vehicleName = vehicleName,
        carType = vehicleType,
        price = price,
        driverId = driverId,
        line = line,
        id = id
    )

    override suspend fun getLines(fetch: Boolean): List<Line> =
        if (localDataSource.lines.hasRecords && !fetch)
            localDataSource.lines.query().toDomain()
        else
            remoteDataSource.getLines()
                .also { localDataSource.lines.insert(it.toEntity()) }
                .toDomain()

    override suspend fun scanManifestQrCode(id: String) = remoteDataSource.scanManifestQrCode(id)

    override suspend fun scanDriverQrCode(id: String) =
        remoteDataSource.scanDriverQrCode(id).toDomain()

    override suspend fun scanVehicleQrCode(id: String) =
        remoteDataSource.scanDispatchQrCode(id).toDomain()

    override suspend fun ocrSpace(image: String, engine: String) =
        remoteDataSource.ocr(image, engine)

    override suspend fun getPrice(locationId: String) =
        remoteDataSource.getPrice(locationId).priceMatrix?.toDomain()

    override suspend fun getVehicleTypes(fetch: Boolean): List<VehicleType> =
        if (localDataSource.vehicleTypes.hasRecords && !fetch)
            localDataSource.vehicleTypes.query().toDomain()
        else
            remoteDataSource.getVehicleTypes().toDomain().also {
                localDataSource.vehicleTypes.insert((it.toEntity()))
            }
}
