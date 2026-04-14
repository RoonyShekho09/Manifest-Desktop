package com.jawharat.manifest.data.remote.datasource

import com.jawharat.manifest.data.remote.model.LineResponse
import com.jawharat.manifest.data.remote.model.Passenger
import com.jawharat.manifest.data.remote.model.SubmitManifestRequestBody
import com.jawharat.manifest.data.remote.model.auth.LoginRequestBody
import com.jawharat.manifest.data.remote.model.auth.LoginResponse
import com.jawharat.manifest.data.remote.model.drivers.AddDriverRequestBody
import com.jawharat.manifest.data.remote.model.drivers.DriverResponse
import com.jawharat.manifest.data.remote.model.ocr.OcrResponse
import com.jawharat.manifest.data.remote.model.dispatches.AddVehicleRequestBody
import com.jawharat.manifest.data.remote.model.dispatches.DispatchResponse
import com.jawharat.manifest.data.remote.model.dispatches.VehicleRemote
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
) : AppRemoteDataSource, BaseRemoteDataSource {

    override suspend fun logout(): Boolean = callApi(
        apiCall = { manifestApiService.logout() },
        mapper = { true }
    ).getOrThrow()

    override suspend fun login(email: String, password: String): LoginResponse =
        manifestApiService.login(body = LoginRequestBody(username = email, password = password))
            .body() ?: throw Exception()

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
        driverId: String?, name: String?,
        phoneNumber: String?, destination: String?
    ) = callApi(
        apiCall = {
            manifestApiService.addDriver(
                body = AddDriverRequestBody(driverId, name, phoneNumber, destination)
            )
        },
        mapper = { it }
    ).getOrThrow()

    override suspend fun addDispatch(
        vehicleNumber: String?, type: String?,
        carType: String?, price: Int?,
        driverId: String?, line: String?
    ) = callApi(
        apiCall = {
            manifestApiService.addDispatch(
                body = AddVehicleRequestBody(vehicleNumber, type, carType, price, driverId, line)
            )
        },
        mapper = { it.dispatch }
    ).getOrThrow()

    override suspend fun editDriver(
        driverId: String?, name: String?,
        phoneNumber: String?, destination: String?,
        id: String
    ) = callApi(
        apiCall = {
            manifestApiService.editDriver(
                body = AddDriverRequestBody(driverId, name, phoneNumber, destination),
                id = id
            )
        },
        mapper = { it }
    ).getOrThrow()

    override suspend fun editVehicle(
        vehicleNumber: String?, vehicleName: String?,
        carType: String?, price: Int?,
        driverId: String?, line: String?,
        id: String
    ) = callApi(
        apiCall = {
            manifestApiService.editVehicle(
                body = AddVehicleRequestBody(
                    vehicleNumber,
                    vehicleName,
                    carType,
                    price,
                    driverId,
                    line
                ),
                id = id
            )
        },
        mapper = { it }
    ).getOrThrow()

    override suspend fun getLines(): List<LineResponse> = callApi(
        apiCall = { manifestApiService.getLines() },
        mapper = { it }
    ).getOrThrow()

    override suspend fun getDispatches(): List<DispatchResponse> = callApi(
        apiCall = { manifestApiService.getVehicles() },
        mapper = { it }
    ).getOrThrow()

    override suspend fun scanManifestQrCode(id: String) = callApi(
        apiCall = { manifestApiService.scanManifestQrCode(id) },
        mapper = { it }
    ).getOrThrow()

    override suspend fun scanDriverQrCode(id: String) = callApi(
        apiCall = { manifestApiService.scanDriverQrCode(id) },
        mapper = { it }
    ).getOrThrow()

    override suspend fun scanDispatchQrCode(id: String) = callApi(
        apiCall = { manifestApiService.scanVehicleQrCode(id) },
        mapper = { it }
    ).getOrThrow()

    override suspend fun getDrivers(): List<DriverResponse> = callApi(
        apiCall = { manifestApiService.getDrivers() },
        mapper = { it }
    ).getOrThrow()

    override suspend fun getVehicleTypes(): List<VehicleRemote> = callApi(
        apiCall = { manifestApiService.getVehicleTypes() },
        mapper = { it }
    ).getOrThrow()

    override suspend fun ocr(image: String, engine: String): OcrResponse {
        val multipart = MultiPartFormDataContent(
            formData {
                append("apikey", "0cd0e66ab388957")
                append("language", "auto")
                append("OCREngine", "2")
                append("base64Image", image)
                append("isOverlayRequired", "true")
                append("scale", "true")
                append("detectOrientation", "true")
            }
        )
        val response = mistralHttpClient.post("https://api.ocr.space/parse/image") {
            setBody(multipart)
        }

        return response.body()
    }

    override suspend fun getPrice(locationId: String) = callApi(
        apiCall = { manifestApiService.getPrice(locationId) },
        mapper = { it }
    ).getOrThrow()

    override suspend fun getUserInformation() = callApi(
        apiCall = { manifestApiService.getUserInformation() },
        mapper = { it }
    ).getOrThrow()
}
