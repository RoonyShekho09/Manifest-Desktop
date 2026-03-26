package com.jawharat.manifest.data.remote.datasource

import com.jawharat.manifest.data.local.model.drivers.DriverQrCodeResponse
import com.jawharat.manifest.data.local.model.drivers.DriverResponse
import com.jawharat.manifest.data.local.model.vehicles.VehicleQrCodeResponse
import com.jawharat.manifest.data.local.model.vehicles.VehicleResponse
import com.jawharat.manifest.data.remote.model.LineResponse
import com.jawharat.manifest.data.remote.model.LoginResponse
import com.jawharat.manifest.data.remote.model.Passenger

interface AppRemoteDataSource {
    suspend fun logout(): Boolean
    suspend fun login(email: String, password: String): LoginResponse
    suspend fun scanManifestQrCode(id: String)
    suspend fun scanDriverQrCode(id: String): DriverQrCodeResponse
    suspend fun scanVehicleQrCode(id: String): VehicleQrCodeResponse
    suspend fun getDrivers(): List<DriverResponse>
    suspend fun getVehicles(): List<VehicleResponse>
    suspend fun submitManifest(
        driverName: String,
        vehicleNumber: String,
        vehicleType: String,
        phoneNumber: String,
        to: String,
        price: String,
        passengers: List<Passenger>,
        driverId: String
    )

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
}
