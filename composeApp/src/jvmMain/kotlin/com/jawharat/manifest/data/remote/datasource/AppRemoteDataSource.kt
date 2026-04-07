package com.jawharat.manifest.data.remote.datasource

import com.jawharat.manifest.data.remote.model.drivers.DriverQrCodeResponse
import com.jawharat.manifest.data.remote.model.drivers.DriverResponse
import com.jawharat.manifest.data.remote.model.vehicles.DispatchQrCodeResponse
import com.jawharat.manifest.data.remote.model.vehicles.DispatchResponse
import com.jawharat.manifest.data.remote.model.LineResponse
import com.jawharat.manifest.data.remote.model.auth.LoginResponse
import com.jawharat.manifest.data.remote.model.Passenger
import com.jawharat.manifest.data.remote.model.vehicles.VehicleRemote

interface AppRemoteDataSource {
    suspend fun logout(): Boolean
    suspend fun login(email: String, password: String): LoginResponse
    suspend fun scanManifestQrCode(id: String)
    suspend fun scanDriverQrCode(id: String): DriverQrCodeResponse
    suspend fun scanDispatchQrCode(id: String): DispatchQrCodeResponse
    suspend fun getDrivers(): List<DriverResponse>
    suspend fun getDispatches(): List<DispatchResponse>
    suspend fun submitManifest(
        driverName: String,
        vehicleNumber: String,
        vehicleType: String,
        phoneNumber: String,
        to: String,
        price: Int,
        passengers: List<Passenger>,
        driverId: String
    ): ByteArray

    suspend fun addDriver(
        driverId: String? = null,
        name: String? = null,
        phoneNumber: String? = null,
        destination: String? = null,
    )

    suspend fun addVehicle(
        vehicleNumber: String? = null,
        type: String? = null,
        carType: String? = null,
        price: Int? = null,
        driverId: String? = null,
        line: String? = null,
    )

    suspend fun editDriver(
        driverId: String? = null,
        name: String? = null,
        phoneNumber: String? = null,
        destination: String? = null,
        id: String
    )

    suspend fun editVehicle(
        vehicleNumber: String? = null,
        type: String? = null,
        carType: String? = null,
        price: Int? = null,
        driverId: String? = null,
        line: String? = null,
        id: String
    )

    suspend fun getLines(): List<LineResponse>
    suspend fun getVehicleTypes(): List<VehicleRemote>
    suspend fun ocr(image: String): String
}
