package com.jawharat.manifest.domain.repository

import com.jawharat.manifest.data.remote.model.PassengerRemote
import com.jawharat.manifest.domain.entity.PersonDocument
import com.jawharat.manifest.domain.entity.manifest.Driver
import com.jawharat.manifest.domain.entity.manifest.DispatchLine
import com.jawharat.manifest.domain.entity.manifest.Dispatch
import com.jawharat.manifest.domain.entity.manifest.DispatchQrResult
import com.jawharat.manifest.domain.entity.manifest.DispatchSummary
import com.jawharat.manifest.domain.entity.manifest.DriverQrResult
import com.jawharat.manifest.domain.entity.Route
import com.jawharat.manifest.domain.entity.UserInformation
import com.jawharat.manifest.domain.entity.manifest.VehicleType

interface ManifestRepository {
    suspend fun getDrivers(fetch: Boolean = true): List<Driver>
    suspend fun getDispatches(fetch: Boolean = true): List<Dispatch>
    suspend fun scanManifestQrCode(id: String, year: String? = null): ByteArray
    suspend fun scanDriverQrCode(id: String): DriverQrResult
    suspend fun scanDispatchQrCode(id: String): DispatchQrResult
    suspend fun submitManifest(
        driverName: String,
        vehicleNumber: String,
        vehicleType: String,
        phoneNumber: String,
        to: String,
        price: Int,
        passengers: List<PassengerRemote>,
        driverId: String
    ): ByteArray

    suspend fun addDriver(
        driverId: String? = null,
        name: String? = null,
        phoneNumber: String? = null,
        destination: String? = null,
    )

    suspend fun addDispatch(
        plateNumber: String? = null,
        vehicleName: String? = null,
        vehicleType: String? = null,
        price: Int? = null,
        driverId: String? = null,
        line: String? = null,
    ): DispatchSummary?

    suspend fun editDriver(
        driverId: String? = null,
        name: String? = null,
        phoneNumber: String? = null,
        destination: String? = null,
        id: String
    )

    suspend fun editDispatch(
        vehicleNumber: String? = null,
        vehicleName: String? = null,
        vehicleType: String? = null,
        price: Int? = null,
        driverId: String? = null,
        line: String? = null,
        id: String
    )

    suspend fun getLines(fetch: Boolean = true): List<DispatchLine>
    suspend fun getVehicleTypes(fetch: Boolean = true): List<VehicleType>
    suspend fun ocr(image: String): PersonDocument
    suspend fun getPrice(locationId: String): List<Route>?
    suspend fun getUserInformation(): UserInformation
}
