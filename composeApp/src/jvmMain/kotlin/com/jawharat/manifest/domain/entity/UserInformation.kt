package com.jawharat.manifest.domain.entity


data class UserInformation(
    val name: String = "",
    val location: UserLocation = UserLocation()
)

data class UserLocation(
    val id: String = "",
    val name: String = ""
)