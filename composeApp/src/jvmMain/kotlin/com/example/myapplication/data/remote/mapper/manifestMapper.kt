package com.example.myapplication.data.remote.mapper

import com.example.myapplication.data.local.model.drivers.DriverResponse
import com.example.myapplication.data.local.model.vehicles.VehicleResponse
import com.example.myapplication.domain.entity.Driver
import com.example.myapplication.domain.entity.DriverInformation
import com.example.myapplication.domain.entity.Line
import com.example.myapplication.domain.entity.Office
import com.example.myapplication.domain.entity.Vehicle

@JvmName("vehicleToDomain")
fun List<VehicleResponse>.toDomain() = map { it.toDomain() }

fun List<DriverResponse>.toDomain() = map { it.toDomain() }

fun DriverResponse.toDomain() = Driver(
    id = id.orEmpty(),
    name = name.orEmpty(),
    phone = phoneNumber.orEmpty(),
    destination = destination.orEmpty()
)

fun VehicleResponse.toDomain() = Vehicle(
    carType = carType.orEmpty(),
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
