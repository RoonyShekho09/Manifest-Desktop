package com.jawharat.manifest.domain.entity

enum class Vehicle(val displayName: String) {
    TAXI("تەکسی"),
    BUS("پاس"),
    OBAMA("ئۆباما"),
    GMC_EXTERNAL("جمسی خارجی"),
    GMC_INTERNAL("جمسی داخلی")
}

data class VehiclePrice(
    val type: Vehicle,
    val price: Int
)

data class Route(
    val routeName: String,
    val prices: List<VehiclePrice>
)
