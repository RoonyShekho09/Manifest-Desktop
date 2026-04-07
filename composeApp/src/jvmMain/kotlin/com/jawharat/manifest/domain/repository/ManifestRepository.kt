package com.jawharat.manifest.domain.repository

import com.jawharat.manifest.data.remote.model.Passenger
import com.jawharat.manifest.data.remote.model.ocr.OcrResponse
import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.domain.entity.Line
import com.jawharat.manifest.domain.entity.Dispatch
import com.jawharat.manifest.domain.entity.VehicleType

interface ManifestRepository {
    suspend fun getDrivers(fetch: Boolean = false): List<Driver>
    suspend fun getDispatches(fetch: Boolean = false): List<Dispatch>
    suspend fun scanManifestQrCode(id: String)
    suspend fun scanDriverQrCode(id: String): Driver
    suspend fun scanVehicleQrCode(id: String): Dispatch
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
        vehicleType: String? = null,
        price: Int? = null,
        driverId: String? = null,
        line: String? = null,
        id: String
    )

    suspend fun getLines(fetch: Boolean = false): List<Line>
    suspend fun getVehicleTypes(fetch: Boolean = false): List<VehicleType>
    suspend fun ocr(image: String): String
    suspend fun ocrSpace(image: String, engine: String): OcrResponse
}
