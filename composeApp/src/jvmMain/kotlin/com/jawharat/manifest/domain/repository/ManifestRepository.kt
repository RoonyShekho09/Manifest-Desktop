package com.jawharat.manifest.domain.repository

import com.jawharat.manifest.data.remote.model.LineResponse
import com.jawharat.manifest.data.remote.model.Passenger
import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.domain.entity.Line
import com.jawharat.manifest.domain.entity.Vehicle

interface ManifestRepository {
    suspend fun getDrivers(fetch: Boolean = false): List<Driver>
    suspend fun getVehicles(fetch: Boolean = false): List<Vehicle>
    suspend fun scanManifestQrCode(id: String)
    suspend fun scanDriverQrCode(id: String): Driver
    suspend fun scanVehicleQrCode(id: String): Vehicle
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

    suspend fun getLines(): List<Line>
}
