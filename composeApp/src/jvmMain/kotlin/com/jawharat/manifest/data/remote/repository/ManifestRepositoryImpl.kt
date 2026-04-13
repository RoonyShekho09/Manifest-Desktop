package com.jawharat.manifest.data.remote.repository

import com.jawharat.manifest.data.local.datasource.AppLocalDataSource
import com.jawharat.manifest.data.remote.proxy.AuthProxy
import com.jawharat.manifest.data.remote.datasource.AppRemoteDataSource
import com.jawharat.manifest.data.remote.mapper.toDomain
import com.jawharat.manifest.data.remote.mapper.toEntity
import com.jawharat.manifest.data.remote.model.Passenger
import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.domain.entity.Dispatch
import com.jawharat.manifest.domain.entity.DispatchLine
import com.jawharat.manifest.domain.entity.VehicleType
import com.jawharat.manifest.domain.repository.ManifestRepository

class ManifestRepositoryImpl(
    proxy: AuthProxy,
    private val remoteDataSource: AppRemoteDataSource,
    private val localDataSource: AppLocalDataSource
) : ManifestRepository, AuthProxy by proxy {

    override suspend fun getDrivers(fetch: Boolean): List<Driver> = authorizedCall {
        if (!fetch && localDataSource.drivers.hasRecords)
            localDataSource.drivers.query().toDomain()
        else
            remoteDataSource.getDrivers().toDomain().also {
                localDataSource.drivers.insert(it.toEntity())
            }
    }

    override suspend fun getDispatches(fetch: Boolean): List<Dispatch> = authorizedCall {
        if (!fetch && localDataSource.dispatches.hasRecords)
            localDataSource.dispatches.query().toDomain()
        else
            remoteDataSource.getDispatches().toDomain().also {
                localDataSource.dispatches.insert(it.toEntity())
            }
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
    ): ByteArray = authorizedCall {
        remoteDataSource.submitManifest(
            driverName = driverName,
            vehicleNumber = vehicleNumber,
            vehicleType = vehicleType,
            phoneNumber = phoneNumber,
            to = to,
            price = price,
            passengers = passengers,
            driverId = driverId
        )
    }

    override suspend fun addDriver(
        driverId: String?,
        name: String?,
        phoneNumber: String?,
        destination: String?
    ) = authorizedCall {
        remoteDataSource.addDriver(
            driverId = driverId,
            name = name,
            phoneNumber = phoneNumber,
            destination = destination
        )
    }

    override suspend fun addVehicle(
        plateNumber: String?,
        vehicleName: String?,
        vehicleType: String?,
        price: Int?,
        driverId: String?,
        line: String?
    ) = authorizedCall {
        remoteDataSource.addVehicle(
            vehicleNumber = plateNumber,
            type = vehicleName,
            carType = vehicleType,
            price = price,
            driverId = driverId,
            line = line
        )
    }

    override suspend fun editDriver(
        driverId: String?,
        name: String?,
        phoneNumber: String?,
        destination: String?,
        id: String
    ) = authorizedCall {
        remoteDataSource.editDriver(
            driverId = driverId,
            name = name,
            phoneNumber = phoneNumber,
            destination = destination,
            id = id
        )
    }

    override suspend fun editVehicle(
        vehicleNumber: String?,
        vehicleName: String?,
        vehicleType: String?,
        price: Int?,
        driverId: String?,
        line: String?,
        id: String
    ) = authorizedCall {
        remoteDataSource.editVehicle(
            vehicleNumber = vehicleNumber,
            vehicleName = vehicleName,
            carType = vehicleType,
            price = price,
            driverId = driverId,
            line = line,
            id = id
        )
    }

    override suspend fun getLines(fetch: Boolean): List<DispatchLine> = authorizedCall {
        if (localDataSource.lines.hasRecords && !fetch)
            localDataSource.lines.query().toDomain()
        else
            remoteDataSource.getLines()
                .also { localDataSource.lines.insert(it.toEntity()) }
                .toDomain()
    }

    override suspend fun scanManifestQrCode(id: String) = authorizedCall {
        remoteDataSource.scanManifestQrCode(id)
    }

    override suspend fun scanDriverQrCode(id: String) = authorizedCall {
        remoteDataSource.scanDriverQrCode(id).toDomain()
    }

    override suspend fun scanVehicleQrCode(id: String) = authorizedCall {
        remoteDataSource.scanDispatchQrCode(id).toDomain()
    }

    override suspend fun ocrSpace(image: String, engine: String) = authorizedCall {
        remoteDataSource.ocr(
            image,
            engine
        ).parsedResults?.firstOrNull()?.textOverlay?.lines?.toDomain()
    }

    override suspend fun getPrice(locationId: String) = authorizedCall {
        remoteDataSource.getPrice(locationId).priceMatrix?.toDomain()
    }

    override suspend fun getVehicleTypes(fetch: Boolean): List<VehicleType> = authorizedCall {
        if (localDataSource.vehicleTypes.hasRecords && !fetch)
            localDataSource.vehicleTypes.query().toDomain()
        else
            remoteDataSource.getVehicleTypes().toDomain().also {
                localDataSource.vehicleTypes.insert((it.toEntity()))
            }
    }
}
