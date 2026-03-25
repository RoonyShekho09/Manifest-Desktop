package com.jawharat.manifest.domain.repository

import com.jawharat.manifest.data.remote.model.Passenger
import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.domain.entity.Vehicle

interface ManifestRepository {
    suspend fun getDrivers(fetch: Boolean = false): List<Driver>
    suspend fun getVehicles(fetch: Boolean = false): List<Vehicle>
    suspend fun scanManifestQrCode(id: String)
    suspend fun scanDriverQrCode(id: String)
    suspend fun scanVehicleQrCode(id: String)
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
