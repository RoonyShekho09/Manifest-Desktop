package com.jawharat.manifest.data.remote.repository

import com.jawharat.manifest.data.remote.proxy.AuthProxy
import com.jawharat.manifest.data.remote.datasource.AppRemoteDataSource
import com.jawharat.manifest.data.remote.mapper.toDomain
import com.jawharat.manifest.data.remote.model.PassengerRemote
import com.jawharat.manifest.domain.entity.manifest.Driver
import com.jawharat.manifest.domain.entity.manifest.Dispatch
import com.jawharat.manifest.domain.entity.manifest.DispatchLine
import com.jawharat.manifest.domain.entity.manifest.VehicleType
import com.jawharat.manifest.domain.repository.ManifestRepository

class ManifestRepositoryImpl(
    proxy: AuthProxy,
    private val remoteDataSource: AppRemoteDataSource,
) : ManifestRepository, AuthProxy by proxy {

    override suspend fun getDrivers(fetch: Boolean): List<Driver> = authorizedCall {
        remoteDataSource.getDrivers().toDomain()
    }

    override suspend fun getDispatches(fetch: Boolean): List<Dispatch> = authorizedCall {
        remoteDataSource.getDispatches().toDomain()
    }

    override suspend fun submitManifest(
        driverName: String,
        vehicleNumber: String,
        vehicleType: String,
        phoneNumber: String,
        to: String,
        price: Int,
        passengers: List<PassengerRemote>,
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

    override suspend fun addDispatch(
        plateNumber: String?,
        vehicleName: String?,
        vehicleType: String?,
        price: Int?,
        driverId: String?,
        line: String?
    ) = authorizedCall {
        remoteDataSource.addDispatch(
            vehicleNumber = plateNumber,
            type = vehicleName,
            carType = vehicleType,
            price = price,
            driverId = driverId,
            line = line
        )
    }?.toDomain()

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

    override suspend fun editDispatch(
        vehicleNumber: String?,
        vehicleName: String?,
        vehicleType: String?,
        price: Int?,
        driverId: String?,
        line: String?,
        id: String
    ) = authorizedCall {
        remoteDataSource.editDispatch(
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
        remoteDataSource.getLines().toDomain()
    }

    override suspend fun scanManifestQrCode(id: String, year: String?) = authorizedCall {
        remoteDataSource.scanManifestQrCode(id, year)
    }

    override suspend fun scanDriverQrCode(id: String) = authorizedCall {
        remoteDataSource.scanDriverQrCode(id).toDomain()
    }

    override suspend fun scanDispatchQrCode(id: String) = authorizedCall {
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

    override suspend fun getUserInformation() = authorizedCall {
        remoteDataSource.getUserInformation().toDomain()
    }

    override suspend fun getVehicleTypes(fetch: Boolean): List<VehicleType> = authorizedCall {
        remoteDataSource.getVehicleTypes().toDomain()
    }
}
