package com.jawharat.manifest.data.remote.datasource

import com.jawharat.manifest.data.local.model.drivers.DriverResponse
import com.jawharat.manifest.data.local.model.vehicles.VehicleResponse
import com.jawharat.manifest.data.remote.model.LoginRequestBody
import com.jawharat.manifest.data.remote.model.LoginResponse
import com.jawharat.manifest.data.remote.model.Passenger
import com.jawharat.manifest.data.remote.model.SubmitManifestRequestBody
import com.jawharat.manifest.data.remote.service.AppApiService

class AppRemoteDataSourceImpl(private val apiService: AppApiService) : AppRemoteDataSource {

    override suspend fun logout(): Boolean {
        apiService.logout()
        return true
    }

    override suspend fun login(
        email: String,
        password: String
    ): LoginResponse =
        apiService.login(body = LoginRequestBody(username = email, password = password)).body()
            ?: throw Exception()


    override suspend fun submitManifest(
        driverName: String,
        vehicleNumber: String,
        vehicleType: String,
        phoneNumber: String,
        to: String,
        price: String,
        passengers: List<Passenger>,
        driverId: String,
    ) {
        apiService.submitManifest(
            body = SubmitManifestRequestBody(
                driverName = driverName,
                vehicleNumber = vehicleNumber,
                vehicleType = vehicleType,
                phoneNumber = phoneNumber,
                to = to,
                price = price,
                passengers = passengers,
                driverId = driverId
            )
        )
    }

    override suspend fun scanManifestQrCode(id: String) =
        apiService.scanManifestQrCode(id).body() ?: throw Exception()

    override suspend fun scanDriverQrCode(id: String) =
        apiService.scanDriverQrCode(id).body() ?: throw Exception()

    override suspend fun scanVehicleQrCode(id: String) =
        apiService.scanVehicleQrCode(id).body() ?: throw Exception()

    override suspend fun getDrivers(): List<DriverResponse> {
        return apiService.getDrivers().body() ?: throw Exception()
    }

    override suspend fun getVehicles(): List<VehicleResponse> {
        return apiService.getVehicles().body() ?: throw Exception()
    }
}
