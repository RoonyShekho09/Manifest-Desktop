package com.example.myapplication.data.remote.datasource

import com.example.myapplication.data.local.model.drivers.DriverResponse
import com.example.myapplication.data.local.model.vehicles.VehicleResponse
import com.example.myapplication.data.remote.model.LoginRequestBody
import com.example.myapplication.data.remote.model.LoginResponse
import com.example.myapplication.data.remote.service.AppApiService

class AppRemoteDataSourceImpl(private val apiService: AppApiService) : AppRemoteDataSource {

    override suspend fun logout(): Boolean {
        apiService.logout()
        return true
    }

    override suspend fun login(
        email: String,
        password: String
    ): LoginResponse {
        apiService.login(body = LoginRequestBody(username = email, password = password))
        return LoginResponse()
    }

    override suspend fun submitManifest(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun scanManifestQrCode(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun scanDriverQrCode(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun scanVehicleQrCode(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getDrivers(): List<DriverResponse> {
        return apiService.getDrivers().body() ?: throw Exception()
    }

    override suspend fun getVehicles(): List<VehicleResponse> {
        return apiService.getVehicles().body() ?: throw Exception()
    }
}
