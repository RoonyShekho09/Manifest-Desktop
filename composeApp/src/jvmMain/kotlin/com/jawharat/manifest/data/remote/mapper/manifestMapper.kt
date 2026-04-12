package com.jawharat.manifest.data.remote.mapper

import com.jawharat.manifest.data.remote.model.drivers.DriverQrCodeResponse
import com.jawharat.manifest.data.remote.model.drivers.DriverResponse
import com.jawharat.manifest.data.remote.model.vehicles.DispatchQrCodeResponse
import com.jawharat.manifest.data.remote.model.vehicles.DispatchResponse
import com.jawharat.manifest.data.remote.model.LineResponse
import com.jawharat.manifest.data.remote.model.PriceMatrix
import com.jawharat.manifest.data.remote.model.RouteDetail
import com.jawharat.manifest.data.remote.model.vehicles.VehicleRemote
import com.jawharat.manifest.db.DispatchRecord
import com.jawharat.manifest.db.DriverRecord
import com.jawharat.manifest.db.LineRecord
import com.jawharat.manifest.db.VehicleRecord
import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.domain.entity.DriverInformation
import com.jawharat.manifest.domain.entity.Line
import com.jawharat.manifest.domain.entity.Office
import com.jawharat.manifest.domain.entity.Dispatch
import com.jawharat.manifest.domain.entity.Route
import com.jawharat.manifest.domain.entity.Vehicle
import com.jawharat.manifest.domain.entity.VehiclePrice
import com.jawharat.manifest.domain.entity.VehicleType

@JvmName("vehicleToDomain")
fun List<DispatchResponse>.toDomain() = map { it.toDomain() }

@JvmName("driverToDomain")
fun List<DriverResponse>.toDomain() = map { it.toDomain() }

fun DriverResponse.toDomain() = Driver(
    id = _id.orEmpty(),
    name = name.orEmpty(),
    phone = phoneNumber.orEmpty(),
    destination = destination.orEmpty(),
    driverId = id.orEmpty()
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
    line = Line(id = line?.id.orEmpty(), name = line?.name.orEmpty()),
    office = Office(office?.id.orEmpty(), office?.name.orEmpty()),
    price = price ?: 0,
    vehicleType = vehicleType.orEmpty(),
    vehicleNumber = vehicleNumber.orEmpty()
)

@JvmName("driverDbToDomain")
fun List<DriverRecord>.toDomain() = map { it.toDomain() }

fun DriverRecord.toDomain() = Driver(
    id = id,
    name = name,
    phone = phone,
    destination = destination,
    driverId = driverId
)

@JvmName("driverToEntity")
fun List<Driver>.toEntity() = map { it.toEntity() }

fun Driver.toEntity() = DriverRecord(
    id = id,
    name = name,
    phone = phone,
    destination = destination,
    driverId = driverId
)

@JvmName("vehicleRecordToDomain")
fun List<DispatchRecord>.toDomain() = map { it.toDomain() }

fun DispatchRecord.toDomain() = Dispatch(
    id = id,
    vehicleName = carType,
    driverInformation = DriverInformation(
        destination = driver_destination,
        _id = driver_id,
        id = driver_id,
        name = driver_name,
        phoneNumber = driver_phone
    ),
    isInside = isInside,
    line = Line(
        id = line_id,
        name = line_name
    ),
    office = Office(
        id = office_id,
        name = office_name
    ),
    price = price,
    vehicleType = type,
    vehicleNumber = vehicleNumber,
)

@JvmName("vehicleToEntity")
fun List<Dispatch>.toEntity() = map { it.toEntity() }

fun Dispatch.toEntity() = DispatchRecord(
    id = id,
    carType = vehicleName,
    driver_destination = driverInformation.destination,
    driver_id = driverInformation.id,
    driver_name = driverInformation.name,
    driver_phone = driverInformation.phoneNumber,
    isInside = isInside,
    line_id = line.id,
    line_name = line.name,
    office_id = office.id,
    office_name = office.name,
    price = price,
    type = vehicleType,
    vehicleNumber = vehicleNumber
)

fun DispatchQrCodeResponse.toDomain() = Dispatch(
    vehicleName = vehicleType.orEmpty(),
    driverInformation = DriverInformation("", "", "", "", ""),
    id = "",
    isInside = false,
    line = Line(name = line.orEmpty(), id = ""),
    office = Office("", ""),
    price = price ?: 0,
    vehicleType = "",
    vehicleNumber = vehicleNumber.orEmpty()
)

fun DriverQrCodeResponse.toDomain() = Driver(
    id = "",
    name = driverName.orEmpty(),
    phone = phoneNumber.orEmpty(),
    destination = to.orEmpty(),
    driverId = driverId.orEmpty()
)

@JvmName("lineRecordToDomain")
fun List<LineRecord>.toDomain() = map { it.toDomain() }

fun LineRecord.toDomain() = Line(
    id = id,
    name = name
)

@JvmName("lineResponseToDomain")
fun List<LineResponse>.toDomain() = map { it.toDomain() }

fun LineResponse.toDomain() = Line(
    id = id.orEmpty(),
    name = name.orEmpty()
)

@JvmName("lineResponseToEntity")
fun List<LineResponse>.toEntity() = map { it.toEntity() }

fun LineResponse.toEntity() = LineRecord(
    id = id.orEmpty(),
    name = name.orEmpty()
)

@JvmName("vehicleRemoteToDomain")
fun List<VehicleRemote>.toDomain() = map { it.toDomain() }

fun VehicleRemote.toDomain() = VehicleType(
    id = id.orEmpty(),
    name = name.orEmpty()
)

fun List<VehicleRecord>.toDomain() = map { it.toDomain() }

fun VehicleRecord.toDomain() = VehicleType(
    id = id,
    name = name
)


fun List<VehicleType>.toEntity() = map { it.toEntity() }

fun VehicleType.toEntity() = VehicleRecord(
    id = id,
    name = name
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