package com.jawharat.manifest.data.remote.service

import com.jawharat.manifest.data.local.model.drivers.DriverResponse
import com.jawharat.manifest.data.local.model.vehicles.VehicleResponse
import com.jawharat.manifest.data.remote.model.LoginRequestBody
import com.jawharat.manifest.data.remote.model.LoginResponse
import com.jawharat.manifest.data.remote.model.SubmitManifestRequestBody
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface AppApiService {

    @POST("login")
    @Headers(NO_AUTH_HEADER)
    suspend fun login(@Body body: LoginRequestBody): Response<LoginResponse>

    @POST("logout")
    suspend fun logout(): Response<LoginResponse>

    @PATCH("manifests/{id}")
    suspend fun submitManifest(@Body body: SubmitManifestRequestBody): Response<Unit>

    @PATCH("manifests/{id}")
    suspend fun scanManifestQrCode(@Path("id") id: String): Response<Unit>

    @GET("drivers/{id}")
    suspend fun scanDriverQrCode(@Path("id") id: String): Response<Unit>

    @GET("vehicles/{id}")
    suspend fun scanVehicleQrCode(@Path("id") id: String): Response<Unit>

    @GET("drivers")
    suspend fun getDrivers(): Response<List<DriverResponse>>

    @GET("vehicles")
    suspend fun getVehicles(): Response<List<VehicleResponse>>

    companion object {
        const val NO_AUTH_HEADER_KEY = "No-Authentication"
        const val NO_AUTH_HEADER = "$NO_AUTH_HEADER_KEY: true"
    }
}
