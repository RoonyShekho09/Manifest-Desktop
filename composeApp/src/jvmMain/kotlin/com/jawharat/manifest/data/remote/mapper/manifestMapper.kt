package com.jawharat.manifest.data.remote.mapper

import com.jawharat.manifest.data.local.model.drivers.DriverQrCodeResponse
import com.jawharat.manifest.data.local.model.drivers.DriverResponse
import com.jawharat.manifest.data.local.model.vehicles.VehicleQrCodeResponse
import com.jawharat.manifest.data.local.model.vehicles.VehicleResponse
import com.jawharat.manifest.data.remote.model.LineResponse
import com.jawharat.manifest.db.DriverRecord
import com.jawharat.manifest.db.LineRecord
import com.jawharat.manifest.db.VehicleRecord
import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.domain.entity.DriverInformation
import com.jawharat.manifest.domain.entity.Line
import com.jawharat.manifest.domain.entity.Office
import com.jawharat.manifest.domain.entity.Vehicle

@JvmName("vehicleToDomain")
fun List<VehicleResponse>.toDomain() = map { it.toDomain() }

@JvmName("driverToDomain")
fun List<DriverResponse>.toDomain() = map { it.toDomain() }

fun DriverResponse.toDomain() = Driver(
    id = _id.orEmpty(),
    name = name.orEmpty(),
    phone = phoneNumber.orEmpty(),
    destination = destination.orEmpty(),
    driverId = id.orEmpty()
)

fun VehicleResponse.toDomain() = Vehicle(
    vehicleType = carType.orEmpty(),
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
    type = type.orEmpty(),
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
fun List<VehicleRecord>.toDomain() = map { it.toDomain() }

fun VehicleRecord.toDomain() = Vehicle(
    id = id,
    vehicleType = carType,
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
    type = type,
    vehicleNumber = vehicleNumber,
)

@JvmName("vehicleToEntity")
fun List<Vehicle>.toEntity() = map { it.toEntity() }

fun Vehicle.toEntity() = VehicleRecord(
    id = id,
    carType = vehicleType,
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
    type = type,
    vehicleNumber = vehicleNumber
)

fun VehicleQrCodeResponse.toDomain() = Vehicle(
    vehicleType = vehicleType.orEmpty(),
    driverInformation = DriverInformation("", "", "", "", ""),
    id = "",
    isInside = false,
    line = Line(name = line.orEmpty(), id = ""),
    office = Office("", ""),
    price = price ?: 0,
    type = "",
    vehicleNumber = vehicleNumber.orEmpty()
)

fun DriverQrCodeResponse.toDomain() = Driver(
    id = driverId.orEmpty(),
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

fun List<LineResponse>.toDomain() = map { it.toDomain() }

fun LineResponse.toDomain() = Line(
    id = id.orEmpty(),
    name = name.orEmpty()
)

fun List<LineResponse>.toEntity() = map { it.toEntity() }

fun LineResponse.toEntity() = LineRecord(
    id = id.orEmpty(),
    name = name.orEmpty()
)
