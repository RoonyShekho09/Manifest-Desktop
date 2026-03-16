package com.example.myapplication.domain.entity

data class Vehicle(
    val carType: String,
    val driverInformation: DriverInformation,
    val id: String,
    val isInside: Boolean,
    val line: Line,
    val office: Office,
    val price: Int,
    val type: String,
    val vehicleNumber: String
)

data class Office(
    val id: String,
    val name: String
)

data class Line(
    val id: String,
    val name: String
)

data class DriverInformation(
    val destination: String,
    val _id: String,
    val id: String,
    val name: String,
    val phoneNumber: String
)
