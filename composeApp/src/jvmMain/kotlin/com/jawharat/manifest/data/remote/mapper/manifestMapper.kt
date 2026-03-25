package com.jawharat.manifest.data.remote.mapper

import com.jawharat.manifest.data.local.model.drivers.DriverQrCodeResponse
import com.jawharat.manifest.data.local.model.drivers.DriverResponse
import com.jawharat.manifest.data.local.model.vehicles.VehicleQrCodeResponse
import com.jawharat.manifest.data.local.model.vehicles.VehicleResponse
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
    destination = destination.orEmpty()
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
fun List<com.jawharat.manifest.db.Driver>.toDomain() = map { it.toDomain() }

fun com.jawharat.manifest.db.Driver.toDomain() = Driver(
    id = id,
    name = name,
    phone = phone,
    destination = destination
)


@JvmName("driverToEntity")
fun List<Driver>.toEntity() = map { it.toEntity() }

fun Driver.toEntity() = com.jawharat.manifest.db.Driver(
    id = id,
    name = name,
    phone = phone,
    destination = destination
)

fun List<com.jawharat.manifest.db.Vehicle>.toDomain() = map { it.toDomain() }

fun com.jawharat.manifest.db.Vehicle.toDomain() = Vehicle(
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

fun List<Vehicle>.toEntity() = map { it.toEntity() }

fun Vehicle.toEntity() = com.jawharat.manifest.db.Vehicle(
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
    destination = to.orEmpty()
)
