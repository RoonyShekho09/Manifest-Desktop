package com.jawharat.manifest.data.remote.mapper

import com.jawharat.manifest.data.remote.model.LineResponse
import com.jawharat.manifest.data.remote.model.PriceMatrix
import com.jawharat.manifest.data.remote.model.RouteDetail
import com.jawharat.manifest.data.remote.model.auth.UserInformationResponse
import com.jawharat.manifest.data.remote.model.dispatches.DispatchQrCodeResponse
import com.jawharat.manifest.data.remote.model.dispatches.DispatchRemote
import com.jawharat.manifest.data.remote.model.dispatches.DispatchResponse
import com.jawharat.manifest.data.remote.model.dispatches.VehicleRemote
import com.jawharat.manifest.data.remote.model.drivers.DriverQrCodeResponse
import com.jawharat.manifest.data.remote.model.drivers.DriverResponse
import com.jawharat.manifest.data.remote.model.ocr.Line
import com.jawharat.manifest.data.remote.model.ocr.OcrResponse
import com.jawharat.manifest.domain.entity.Dispatch
import com.jawharat.manifest.domain.entity.DispatchLine
import com.jawharat.manifest.domain.entity.DispatchQrResult
import com.jawharat.manifest.domain.entity.DispatchSummary
import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.domain.entity.DriverInformation
import com.jawharat.manifest.domain.entity.DriverQrResult
import com.jawharat.manifest.domain.entity.OcrLine
import com.jawharat.manifest.domain.entity.Office
import com.jawharat.manifest.data.remote.model.ocr.Line
import com.jawharat.manifest.domain.entity.manifest.Dispatch
import com.jawharat.manifest.domain.entity.manifest.DispatchLine
import com.jawharat.manifest.domain.entity.manifest.DispatchQrResult
import com.jawharat.manifest.domain.entity.manifest.DispatchSummary
import com.jawharat.manifest.domain.entity.manifest.Driver
import com.jawharat.manifest.domain.entity.manifest.DriverInformation
import com.jawharat.manifest.domain.entity.manifest.DriverQrResult
import com.jawharat.manifest.domain.entity.ocr.OcrLine
import com.jawharat.manifest.domain.entity.manifest.Office
import com.jawharat.manifest.domain.entity.Route
import com.jawharat.manifest.domain.entity.UserInformation
import com.jawharat.manifest.domain.entity.UserLocation
import com.jawharat.manifest.domain.entity.Vehicle
import com.jawharat.manifest.domain.entity.VehiclePrice
import com.jawharat.manifest.domain.entity.VehicleType
import com.jawharat.manifest.domain.entity.PersonDocument
import com.jawharat.manifest.domain.entity.Word
import com.jawharat.manifest.utils.allCountries
import com.jawharat.manifest.domain.entity.manifest.VehicleType
import com.jawharat.manifest.domain.entity.ocr.OcrWord
import com.jawharat.manifest.utils.orZero

@JvmName("vehicleToDomain")
fun List<DispatchResponse>.toDomain() = map { it.toDomain() }

@JvmName("driverToDomain")
fun List<DriverResponse>.toDomain() = map { it.toDomain() }

fun DriverResponse.toDomain() = Driver(
    id = _id.orEmpty(),
    name = name.orEmpty(),
    phoneNumber = phoneNumber.orEmpty(),
    destination = destination.orEmpty(),
    driverId = id.orEmpty(),
    blocked = blocked
)

fun DispatchResponse.toDomain() = Dispatch(
    vehicleName = vehicleName.orEmpty(),
    driverInformation = DriverInformation(
        destination = driverId?.destination.orEmpty(),
        _id = driverId?._id.orEmpty(),
        id = driverId?.id.orEmpty(),
        name = driverId?.name.orEmpty(),
        phoneNumber = driverId?.phoneNumber.orEmpty()
    ),
    id = id.orEmpty(),
    isInside = isInside ?: false,
    dispatchLine = DispatchLine(id = line?.id.orEmpty(), name = line?.name.orEmpty()),
    office = Office(office?.id.orEmpty(), office?.name.orEmpty()),
    price = price ?: 0,
    vehicleType = vehicleType.orEmpty(),
    plateNumber = vehicleNumber.orEmpty(),
    blocked = blocked
)

fun DispatchQrCodeResponse.toDomain() = DispatchQrResult(
    line = line.orEmpty(),
    price = price.orZero(),
    plateNumber = vehicleNumber.orEmpty(),
    vehicleName = vehicleName.orEmpty()
)

fun DriverQrCodeResponse.toDomain() = DriverQrResult(
    name = driverName.orEmpty(),
    phoneNumber = phoneNumber.orEmpty(),
    destination = to.orEmpty(),
    driverId = driverId.orEmpty(),
    blocked = blocked
)


@JvmName("lineResponseToDomain")
fun List<LineResponse>.toDomain() = map { it.toDomain() }

fun LineResponse.toDomain() = DispatchLine(
    id = id.orEmpty(),
    name = name.orEmpty()
)

@JvmName("vehicleRemoteToDomain")
fun List<VehicleRemote>.toDomain() = map { it.toDomain() }

fun VehicleRemote.toDomain() = VehicleType(
    id = id.orEmpty(),
    name = name.orEmpty()
)


fun PriceMatrix.toDomain(): List<Route> = listOfNotNull(
    erbilBaghdad?.let { Route("هەولێر - بەغداد", it.toDomainPrices()) },
    erbilKhanaqin?.let { Route("هەولێر - خانەقین", it.toDomainPrices()) },
    erbilSulaymaniyah?.let { Route("هەولێر - سلێمانی", it.toDomainPrices()) },
    erbilPerde?.let { Route("هەولێر - پردێ", it.toDomainPrices()) },
    erbilChamchamal?.let { Route("هەولێر - چەمچەماڵ", it.toDomainPrices()) },
    erbilRamadi?.let { Route("هەولێر - ڕومادی", it.toDomainPrices()) },
    erbilKifri?.let { Route("هەولێر - کفری", it.toDomainPrices()) },
    erbilKirkuk?.let { Route("هەولێر - کەرکووک", it.toDomainPrices()) },
    erbilKalar?.let { Route("هەولێر - کەلار", it.toDomainPrices()) }
).filter { it.prices.isNotEmpty() }

fun RouteDetail.toDomainPrices(): List<VehiclePrice> = listOfNotNull(
    taxi?.let { VehiclePrice(Vehicle.TAXI, it) },
    bus?.let { VehiclePrice(Vehicle.BUS, it) },
    obama?.let { VehiclePrice(Vehicle.OBAMA, it) },
    gmcExternal?.let { VehiclePrice(Vehicle.GMC_EXTERNAL, it) },
    gmcInternal?.let { VehiclePrice(Vehicle.GMC_INTERNAL, it) }
)

fun UserInformationResponse.toDomain() = UserInformation(
    name = name.orEmpty(),
    location = UserLocation(id = location?.id.orEmpty(), name = location?.name.orEmpty())
)

fun DispatchRemote.toDomain() =
    DispatchSummary(
        id = id.orEmpty(),
        driverId = driverId.orEmpty(),
        isInside = isInside == true,
        price = price.orZero(),
        vehicleName = vehicleName.orEmpty(),
        plateNumber = vehicleNumber.orEmpty(),
        vehicleType = vehicleType.orEmpty(),
        line = line.orEmpty(),
    )


fun List<Line>.toDomain() = map { it.toDomain() }

fun Line.toDomain() = OcrLine(
    text = lineText.orEmpty(),
    maxHeight = maxHeight,
    minTop = minTop,
    ocrWords = words.map {
        OcrWord(
            height = it.height,
            left = it.left,
            top = it.top,
            width = it.width,
            wordText = it.wordText
        )
    }
)
