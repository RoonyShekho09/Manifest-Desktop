package com.jawharat.manifest.data.remote.service

import com.jawharat.manifest.data.remote.model.LineResponse
import com.jawharat.manifest.data.remote.model.PriceResponse
import com.jawharat.manifest.data.remote.model.SubmitManifestRequestBody
import com.jawharat.manifest.data.remote.model.auth.LoginRequestBody
import com.jawharat.manifest.data.remote.model.auth.LoginResponse
import com.jawharat.manifest.data.remote.model.auth.UserInformationResponse
import com.jawharat.manifest.data.remote.model.drivers.AddDriverRequestBody
import com.jawharat.manifest.data.remote.model.drivers.DriverQrCodeResponse
import com.jawharat.manifest.data.remote.model.drivers.DriverResponse
import com.jawharat.manifest.data.remote.model.vehicles.AddVehicleRequestBody
import com.jawharat.manifest.data.remote.model.vehicles.DispatchQrCodeResponse
import com.jawharat.manifest.data.remote.model.vehicles.DispatchResponse
import com.jawharat.manifest.data.remote.model.vehicles.VehicleRemote
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.statement.HttpResponse

interface ManifestApiService {

    @POST("login")
    @Headers(NO_AUTH_HEADER)
    suspend fun login(@Body body: LoginRequestBody): Response<LoginResponse>

    @POST("logout")
    suspend fun logout(): Response<LoginResponse>

    @POST("manifests")
    suspend fun submitManifest(
        @Body body: SubmitManifestRequestBody,
        @Header("Accept") accept: String = "application/pdf",
    ): HttpResponse

    @PATCH("manifests/{id}")
    suspend fun scanManifestQrCode(@Path("id") id: String): Response<Unit>

    @GET("drivers/{id}")
    suspend fun scanDriverQrCode(@Path("id") id: String): Response<DriverQrCodeResponse>

    @GET("vehicles/{id}")
    suspend fun scanVehicleQrCode(@Path("id") id: String): Response<DispatchQrCodeResponse>

    @GET("drivers")
    suspend fun getDrivers(): Response<List<DriverResponse>>

    @GET("vehicles")
    suspend fun getVehicles(): Response<List<DispatchResponse>>

    @POST("drivers")
    suspend fun addDriver(@Body body: AddDriverRequestBody): Response<Unit>

    @POST("vehicles")
    suspend fun addVehicle(@Body body: AddVehicleRequestBody): Response<Unit>

    @PUT("drivers/{id}")
    suspend fun editDriver(@Body body: AddDriverRequestBody, @Path("id") id: String): Response<Unit>

    @PUT("vehicles/{id}")
    suspend fun editVehicle(
        @Body body: AddVehicleRequestBody,
        @Path("id") id: String
    ): Response<Unit>

    @GET("lines")
    suspend fun getLines(): Response<List<LineResponse>>

    @GET("car-types")
    suspend fun getVehicleTypes(): Response<List<VehicleRemote>>

    @GET("prices/location/{locationId}")
    suspend fun getPrice(@Path("locationId") locationId: String): Response<PriceResponse>

    @GET("me")
    suspend fun getUserInformation(): Response<UserInformationResponse>

    companion object {
        const val NO_AUTH_HEADER_KEY = "No-Authentication"
        const val NO_AUTH_HEADER = "$NO_AUTH_HEADER_KEY: true"
    }
}
