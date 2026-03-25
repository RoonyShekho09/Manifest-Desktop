package com.jawharat.manifest.data.remote.datasource

import com.jawharat.manifest.data.local.model.drivers.DriverResponse
import com.jawharat.manifest.data.local.model.vehicles.VehicleResponse
import com.jawharat.manifest.data.remote.model.LoginResponse
import com.jawharat.manifest.data.remote.model.Passenger

interface AppRemoteDataSource {
    suspend fun logout(): Boolean
    suspend fun login(email: String, password: String): LoginResponse
    suspend fun scanManifestQrCode(id: String)
    suspend fun scanDriverQrCode(id: String)
    suspend fun scanVehicleQrCode(id: String)
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
}