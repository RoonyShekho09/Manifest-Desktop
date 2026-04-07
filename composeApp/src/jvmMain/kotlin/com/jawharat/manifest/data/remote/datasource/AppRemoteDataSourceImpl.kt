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
import com.jawharat.manifest.data.remote.service.Document
import com.jawharat.manifest.data.remote.service.MistralApiService
import com.jawharat.manifest.data.remote.service.MistralRequestBody
import com.jawharat.manifest.data.remote.service.OcrApiService
import com.jawharat.manifest.di.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class AppRemoteDataSourceImpl(
    private val apiService: AppApiService,
    private val ocrApiService: OcrApiService,
    private val httpClient: HttpClient,
    private val mistralApiService: MistralApiService,
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
        price: Int,
        passengers: List<Passenger>,
        driverId: String,
    ): ByteArray {
        val response = httpClient.post(BASE_URL + "manifests") {
            header(HttpHeaders.Accept, "application/pdf")
            contentType(ContentType.Application.Json)
            setBody(
                Json.encodeToString(
                    SubmitManifestRequestBody(
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
            )
        }
        if (response.status == HttpStatusCode.OK)
            return response.readRawBytes()
        else
            throw Exception(response.bodyAsText())
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

    override suspend fun mistralOcr(image: String): String {
        return mistralApiService.ocr(
            body = MistralRequestBody(document = Document(documentUrl = image))
        ).body().toString()
    }

    override suspend fun ocr(image: String): String {
        val multipart = MultiPartFormDataContent(
            formData {
                append("apikey", "0cd0e66ab388957")
                append("OCREngine", "3")
                append("base64Image", image)
            }
        )

        return httpClient.post("https://api.ocr.space/parse/image") {
            setBody(multipart)
        }.body<Json>().toString()
    }
}
