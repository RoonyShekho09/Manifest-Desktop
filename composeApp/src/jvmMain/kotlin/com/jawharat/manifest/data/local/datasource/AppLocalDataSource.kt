package com.jawharat.manifest.data.local.datasource

import com.jawharat.manifest.data.local.factory.EntityStore
import com.jawharat.manifest.data.local.model.UserLocal
import com.jawharat.manifest.db.DispatchRecord
import com.jawharat.manifest.db.DriverRecord
import com.jawharat.manifest.db.LineRecord
import com.jawharat.manifest.db.VehicleRecord

interface AppLocalDataSource {
    val token: String?
    val lastUsedEmail: String?
    fun storeToken(value: String)
    fun storeLastUsedEmail(value: String)
    fun storeUser(value: UserLocal)
    fun clearDataStore()

    val drivers: EntityStore<DriverRecord>
    val dispatches: EntityStore<DispatchRecord>
    val lines: EntityStore<LineRecord>
    val vehicleTypes: EntityStore<VehicleRecord>
}