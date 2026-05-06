package com.jawharat.manifest.data.remote.datasource

import com.jawharat.manifest.data.remote.model.LineResponse
import com.jawharat.manifest.data.remote.model.Passenger
import com.jawharat.manifest.data.remote.model.SubmitManifestRequestBody
import com.jawharat.manifest.data.remote.model.auth.LoginRequestBody
import com.jawharat.manifest.data.remote.model.auth.LoginResponse
import com.jawharat.manifest.data.remote.model.dispatches.AddVehicleRequestBody
import com.jawharat.manifest.data.remote.model.dispatches.DispatchResponse
import com.jawharat.manifest.data.remote.model.dispatches.VehicleRemote
import com.jawharat.manifest.data.remote.model.drivers.AddDriverRequestBody
import com.jawharat.manifest.data.remote.model.drivers.DriverResponse
import com.jawharat.manifest.data.remote.model.ocr.OcrResponse
import com.jawharat.manifest.data.remote.service.ManifestApiService
import com.jawharat.manifest.di.BASE_URL
import com.jawharat.manifest.domain.entity.UpdateInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class AppRemoteDataSourceImpl(
    private val manifestApiService: ManifestApiService,
    private val pdfHttpClient: HttpClient,
    private val checkUpdatesClient: HttpClient,
    private val ocrClient: HttpClient
) : AppRemoteDataSource, BaseRemoteDataSource {

    override suspend fun getUpdateInfo(currentVersion: String, versionFileUrl: String): UpdateInfo {
        return withContext(Dispatchers.IO) {
            try {
                val response: HttpResponse = checkUpdatesClient.get(versionFileUrl)
                val latestVersion = response.bodyAsText().trim()
                parseUpdateInfo(latestVersion)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    private fun parseUpdateInfo(text: String): UpdateInfo {
        val build = Regex("""BUILD_NUMBER\s*=\s*(\d+)""")
            .find(text)?.groupValues?.get(1)?.toIntOrNull() ?: 0

        val isForced = Regex("""IS_FORCED\s*=\s*(true|false)""", RegexOption.IGNORE_CASE)
            .find(text)?.groupValues?.get(1)?.toBoolean() ?: false

        val minBuild = Regex("""MINIMUM_BUILD_NUMBER\s*=\s*(\d+)""")
            .find(text)?.groupValues?.get(1)?.toIntOrNull() ?: 0

        return UpdateInfo(build, isForced, minBuild)
    }

    override suspend fun logout(): Boolean = callApi(
        apiCall = { manifestApiService.logout() },
        mapper = { true }
    ).getOrThrow()

    override suspend fun login(email: String, password: String): LoginResponse = callApi(
        apiCall = {
            manifestApiService.login(body = LoginRequestBody(username = email, password = password))
        },
        mapper = { it }
    ).getOrThrow()

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
        when (response.status) {
            HttpStatusCode.OK -> return response.readRawBytes()
            HttpStatusCode.TooManyRequests -> throw response.toTooManyRequestsException()
            else -> throw Exception(response.bodyAsText())
        }
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

    override suspend fun editDispatch(
        vehicleNumber: String?, vehicleName: String?,
        carType: String?, price: Int?,
        driverId: String?, line: String?,
        id: String
    ) = callApi(
        apiCall = {
            manifestApiService.editDispatch(
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
        apiCall = { manifestApiService.getDispatches() },
        mapper = { it }
    ).getOrThrow()

    override suspend fun scanManifestQrCode(id: String, year: String?): ByteArray {
        val response = pdfHttpClient.patch(BASE_URL + "manifests/$year/$id") {
            header(HttpHeaders.Accept, "application/pdf")
            contentType(ContentType.Application.Json)
        }
        when (response.status.value) {
            in 200..299 -> return response.readRawBytes()
            429 -> throw response.toTooManyRequestsException()
            else -> throw Exception(response.bodyAsText())
        }
    }

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

    override suspend fun ocr(image: String): OcrResponse {
        val response = ocrClient.post("${BASE_URL}ocr") {
            contentType(ContentType.Text.Plain)
            accept(ContentType.Application.Json)
            setBody(image)
        }

        return response.body() ?: throw Exception()
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
