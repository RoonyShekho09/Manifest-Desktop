package com.jawharat.manifest.data.remote.datasource

import com.jawharat.manifest.data.remote.model.LineResponse
import com.jawharat.manifest.data.remote.model.Passenger
import com.jawharat.manifest.data.remote.model.SubmitManifestRequestBody
import com.jawharat.manifest.data.remote.model.auth.LoginRequestBody
import com.jawharat.manifest.data.remote.model.auth.LoginResponse
import com.jawharat.manifest.data.remote.model.drivers.AddDriverRequestBody
import com.jawharat.manifest.data.remote.model.drivers.DriverResponse
import com.jawharat.manifest.data.remote.model.ocr.OcrResponse
import com.jawharat.manifest.data.remote.model.vehicles.AddVehicleRequestBody
import com.jawharat.manifest.data.remote.model.vehicles.DispatchResponse
import com.jawharat.manifest.data.remote.model.vehicles.VehicleRemote
import com.jawharat.manifest.data.remote.service.ManifestApiService
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
    private val manifestApiService: ManifestApiService,
    private val pdfHttpClient: HttpClient,
    private val mistralHttpClient: HttpClient,
) : AppRemoteDataSource {

    override suspend fun logout(): Boolean {
        val response = manifestApiService.logout()
        return response.isSuccessful
    }

    override suspend fun login(
        email: String,
        password: String
    ): LoginResponse =
        manifestApiService.login(body = LoginRequestBody(username = email, password = password))
            .body()
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
        val response = pdfHttpClient.post(BASE_URL + "manifests") {
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
    ) = manifestApiService.addDriver(
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
    ) = manifestApiService.addVehicle(
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
        val result = manifestApiService.editDriver(
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
    ) = manifestApiService.editVehicle(
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
        manifestApiService.getLines().body() ?: throw Exception()

    override suspend fun getDispatches(): List<DispatchResponse> =
        manifestApiService.getVehicles().body() ?: throw Exception()

    override suspend fun scanManifestQrCode(id: String) =
        manifestApiService.scanManifestQrCode(id).body() ?: throw Exception()

    override suspend fun scanDriverQrCode(id: String) =
        manifestApiService.scanDriverQrCode(id).body() ?: throw Exception()

    override suspend fun scanDispatchQrCode(id: String) =
        manifestApiService.scanVehicleQrCode(id).body() ?: throw Exception()

    override suspend fun getDrivers(): List<DriverResponse> =
        manifestApiService.getDrivers().body() ?: throw Exception()

    override suspend fun getVehicleTypes(): List<VehicleRemote> =
        manifestApiService.getVehicleTypes().body() ?: throw Exception()

    override suspend fun ocr(image: String, engine: String): OcrResponse {
        val multipart = MultiPartFormDataContent(
            formData {
                append("apikey", "0cd0e66ab388957")
                append("language", "auto")
                append("OCREngine", 2)
                append("base64Image", image)
                append("isOverlayRequired", true)
                append("scale", true)
                append("detectOrientation", true)
            }
        )

        return mistralHttpClient.post("https://api.ocr.space/parse/image") {
            setBody(multipart)
        }.body<OcrResponse>()
    }
}
