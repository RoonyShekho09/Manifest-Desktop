package com.jawharat.manifest.data.local.datasource

import com.jawharat.manifest.data.local.model.vehicles.UserLocal
import com.jawharat.manifest.db.Driver
import com.jawharat.manifest.db.Vehicle

interface AppLocalDataSource {
    val token: String?
    val hasVehiclesInDb: Boolean
    val hasDriversInDb: Boolean
    fun storeToken(value: String)
    fun storeUser(value: UserLocal)
    fun clearDataStore()
    fun insertDrivers(drivers: List<Driver>)
    fun queryDrivers(): List<Driver>
    fun queryVehicles(): List<Vehicle>
    fun insertVehicles(vehicles: List<Vehicle>)
}