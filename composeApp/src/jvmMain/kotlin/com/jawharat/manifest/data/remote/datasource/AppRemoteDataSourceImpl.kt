package com.jawharat.manifest.data.remote.datasource

import com.jawharat.manifest.data.remote.model.LineResponse
import com.jawharat.manifest.data.remote.model.Passenger
import com.jawharat.manifest.data.remote.model.SubmitManifestRequestBody
import com.jawharat.manifest.data.remote.model.auth.LoginRequestBody
import com.jawharat.manifest.data.remote.model.auth.LoginResponse
import com.jawharat.manifest.data.remote.model.drivers.AddDriverRequestBody
import com.jawharat.manifest.data.remote.model.drivers.DriverResponse
import com.jawharat.manifest.data.remote.model.vehicles.AddVehicleRequestBody
import com.jawharat.manifest.data.remote.model.vehicles.DispatchResponse
import com.jawharat.manifest.data.remote.model.vehicles.VehicleRemote
import com.jawharat.manifest.data.remote.service.AppApiService
import com.jawharat.manifest.di.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class AppRemoteDataSourceImpl(
    private val apiService: AppApiService,
    private val httpClient: HttpClient,
) : AppRemoteDataSource {

    override suspend fun logout(): Boolean {
        val response = apiService.logout()
        return response.isSuccessful
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
        val pdfBytes = httpClient.post(BASE_URL + "manifests") {
            header(HttpHeaders.Accept, "application/pdf")
            contentType(ContentType.Application.Json)
            setBody(
                Json.encodeToString(
                    SubmitManifestRequestBody(
                        driverName = "سجاد واثق جبار",
                        vehicleNumber = "22A43942",
                        vehicleType = "جمسی داخلی",
                        phoneNumber = "٠٧٧٠٩٢٠١٨٤٧",
                        to = "بەغداد",
                        price = 12000,
                        passengers = passengers,
                        driverId = "199619559031"
                    )
                )
            )
        }.readRawBytes()
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

    override suspend fun getDispatches(): List<DispatchResponse> =
        apiService.getVehicles().body() ?: throw Exception()

    override suspend fun scanManifestQrCode(id: String) =
        apiService.scanManifestQrCode(id).body() ?: throw Exception()

    override suspend fun scanDriverQrCode(id: String) =
        apiService.scanDriverQrCode(id).body() ?: throw Exception()

    override suspend fun scanDispatchQrCode(id: String) =
        apiService.scanVehicleQrCode(id).body() ?: throw Exception()

    override suspend fun getDrivers(): List<DriverResponse> =
        apiService.getDrivers().body() ?: throw Exception()

    override suspend fun getVehicleTypes(): List<VehicleRemote> =
        apiService.getVehicleTypes().body() ?: throw Exception()
}
