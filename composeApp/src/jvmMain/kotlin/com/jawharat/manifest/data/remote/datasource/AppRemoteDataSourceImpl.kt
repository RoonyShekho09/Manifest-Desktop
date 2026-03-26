package com.jawharat.manifest.data.remote.datasource

import com.jawharat.manifest.data.local.model.drivers.DriverResponse
import com.jawharat.manifest.data.local.model.vehicles.VehicleResponse
import com.jawharat.manifest.data.remote.model.AddDriverRequestBody
import com.jawharat.manifest.data.remote.model.AddVehicleRequestBody
import com.jawharat.manifest.data.remote.model.LineResponse
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

    override suspend fun addDriver(
        driverId: String?,
        name: String?,
        phoneNumber: String?,
        destination: String?
    ) = apiService.addDriver(
        body = AddDriverRequestBody(
            driverId = driverId,
            name = name,
            phoneNumber = phoneNumber,
            destination = destination
        )
    ).body() ?: throw Exception()

    override suspend fun addVehicle(
        vehicleNumber: String?,
        type: String?,
        carType: String?,
        price: Int?,
        driverId: String?,
        line: String?
    ) = apiService.addVehicle(
        body = AddVehicleRequestBody(
            vehicleNumber = vehicleNumber,
            type = type,
            carType = carType,
            price = price,
            driverId = driverId,
            line = line
        )
    ).body() ?: throw Exception()

    override suspend fun editDriver(
        driverId: String?,
        name: String?,
        phoneNumber: String?,
        destination: String?,
        id: String
    ) {
        val result = apiService.editDriver(
            body = AddDriverRequestBody(
                driverId = driverId,
                name = name,
                phoneNumber = phoneNumber,
                destination = destination
            ),
            id = id
        )

        println("error: ${result.message}")
        return result.body() ?: throw Exception(result.message)
    }

    override suspend fun editVehicle(
        vehicleNumber: String?,
        type: String?,
        carType: String?,
        price: Int?,
        driverId: String?,
        line: String?,
        id: String
    ) = apiService.editVehicle(
        body = AddVehicleRequestBody(
            vehicleNumber = vehicleNumber,
            type = type,
            carType = carType,
            price = price,
            driverId = driverId,
            line = line
        ),
        id = id
    ).body() ?: throw Exception()

    override suspend fun getLines(): List<LineResponse> =
        apiService.getLines().body() ?: throw Exception()

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
