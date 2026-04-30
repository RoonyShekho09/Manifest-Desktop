package com.jawharat.manifest.data.remote.datasource

import com.jawharat.manifest.data.remote.model.drivers.DriverQrCodeResponse
import com.jawharat.manifest.data.remote.model.drivers.DriverResponse
import com.jawharat.manifest.data.remote.model.dispatches.DispatchQrCodeResponse
import com.jawharat.manifest.data.remote.model.dispatches.DispatchResponse
import com.jawharat.manifest.data.remote.model.LineResponse
import com.jawharat.manifest.data.remote.model.auth.LoginResponse
import com.jawharat.manifest.data.remote.model.Passenger
import com.jawharat.manifest.data.remote.model.PriceResponse
import com.jawharat.manifest.data.remote.model.auth.UserInformationResponse
import com.jawharat.manifest.data.remote.model.dispatches.DispatchRemote
import com.jawharat.manifest.data.remote.model.dispatches.VehicleRemote
import com.jawharat.manifest.data.remote.model.ocr.OcrResponse
import com.jawharat.manifest.domain.entity.UpdateInfo

interface AppRemoteDataSource {
    suspend fun logout(): Boolean
    suspend fun login(email: String, password: String): LoginResponse
    suspend fun scanManifestQrCode(id: String, year: String? = null): ByteArray
    suspend fun scanDriverQrCode(id: String): DriverQrCodeResponse
    suspend fun scanDispatchQrCode(id: String): DispatchQrCodeResponse
    suspend fun getDrivers(): List<DriverResponse>
    suspend fun getDispatches(): List<DispatchResponse>
    suspend fun submitManifest(
        driverName: String,
        vehicleNumber: String,
        vehicleType: String,
        phoneNumber: String,
        to: String,
        price: Int,
        passengers: List<Passenger>,
        driverId: String
    ): ByteArray

    suspend fun addDriver(
        driverId: String? = null,
        name: String? = null,
        phoneNumber: String? = null,
        destination: String? = null,
    )

    suspend fun addDispatch(
        vehicleNumber: String? = null,
        type: String? = null,
        carType: String? = null,
        price: Int? = null,
        driverId: String? = null,
        line: String? = null,
    ): DispatchRemote?

    suspend fun editDriver(
        driverId: String? = null,
        name: String? = null,
        phoneNumber: String? = null,
        destination: String? = null,
        id: String
    )

    suspend fun editDispatch(
        vehicleNumber: String? = null,
        vehicleName: String? = null,
        carType: String? = null,
        price: Int? = null,
        driverId: String? = null,
        line: String? = null,
        id: String
    )

    suspend fun getLines(): List<LineResponse>
    suspend fun getVehicleTypes(): List<VehicleRemote>
    suspend fun ocr(image: String): OcrResponse
    suspend fun getPrice(locationId: String): PriceResponse
    suspend fun getUserInformation(): UserInformationResponse
    suspend fun getUpdateInfo(currentVersion: String, versionFileUrl: String): UpdateInfo
}
