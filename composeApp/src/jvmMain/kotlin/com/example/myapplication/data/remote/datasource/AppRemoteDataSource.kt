package com.example.myapplication.data.remote.datasource

import com.example.myapplication.data.local.model.drivers.DriverResponse
import com.example.myapplication.data.local.model.vehicles.VehicleResponse
import com.example.myapplication.data.remote.model.LoginResponse

interface AppRemoteDataSource {
    suspend fun logout(): Boolean
    suspend fun login(email: String, password: String): LoginResponse

    suspend fun submitManifest(id: String)

    suspend fun scanManifestQrCode(id: String)

    suspend fun scanDriverQrCode(id: String)

    suspend fun scanVehicleQrCode(id: String)

    suspend fun getDrivers(): List<DriverResponse>

    suspend fun getVehicles(): List<VehicleResponse>
}