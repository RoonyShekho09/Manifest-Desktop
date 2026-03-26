package com.jawharat.manifest.data.local.datasource

import com.jawharat.manifest.data.local.model.vehicles.UserLocal
import com.jawharat.manifest.data.remote.model.LineResponse
import com.jawharat.manifest.db.DriverRecord
import com.jawharat.manifest.db.LineRecord
import com.jawharat.manifest.db.VehicleRecord

interface AppLocalDataSource {
    val token: String?
    val hasVehiclesInDb: Boolean
    val hasDriversInDb: Boolean
    fun storeToken(value: String)
    fun storeUser(value: UserLocal)
    fun clearDataStore()
    fun insertDrivers(drivers: List<DriverRecord>)
    fun insertLines(lines: List<LineRecord>)
    fun queryDrivers(): List<DriverRecord>
    fun queryVehicles(): List<VehicleRecord>
    fun insertVehicles(vehicles: List<VehicleRecord>)
    fun queryLines(): List<LineRecord>
    val hasLinesInDb: Boolean
}