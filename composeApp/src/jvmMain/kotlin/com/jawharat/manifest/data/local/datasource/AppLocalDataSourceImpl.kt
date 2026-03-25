package com.jawharat.manifest.data.local.datasource

import com.jawharat.manifest.data.local.model.vehicles.UserLocal
import com.jawharat.manifest.db.Driver
import com.jawharat.manifest.db.ManifestDatabase
import com.jawharat.manifest.db.Vehicle
import com.jawharat.manifest.utils.putObject


class AppLocalDataSourceImpl(
    private val settings: AppSettings = AppSettings,
    private val database: ManifestDatabase
) : AppLocalDataSource {

    override val token: String?
        get() = settings.getString(TOKEN_KEY)

    override fun storeToken(value: String) = settings.putObject(TOKEN_KEY, value)

    override fun storeUser(value: UserLocal) = settings.putObject(USER_KEY, value)

    override fun clearDataStore() = settings.clear()

    override fun insertDrivers(drivers: List<Driver>) {
        database.transaction {
            drivers.forEach { driver ->
                database.driversQueries.insertDriver(
                    id = driver.id,
                    name = driver.name,
                    phone = driver.phone,
                    destination = driver.destination
                )
            }
        }
    }

    override fun insertVehicles(vehicles: List<Vehicle>) {
        database.transaction {
            vehicles.forEach { driver ->
                database.vehiclesQueries.insertVehicle(
                    id = driver.id,
                    carType = driver.carType,
                    isInside = driver.isInside,
                    price = driver.price,
                    type = driver.type,
                    vehicleNumber = driver.vehicleNumber,
                    driver_id = driver.driver_id,
                    driver_name = driver.driver_name,
                    driver_phone = driver.driver_phone,
                    driver_destination = driver.driver_destination,
                    office_id = driver.office_name,
                    office_name = driver.office_name,
                    line_id = driver.line_id,
                    line_name = driver.line_name,
                )
            }
        }
    }

    override val hasVehiclesInDb: Boolean
        get() = run {
            val hasVehicles = database.vehiclesQueries.hasVehicles()
            println("hasVehicles: ${hasVehicles.executeAsList()}")
            hasVehicles.executeAsOne()
        }

    override val hasDriversInDb: Boolean
        get() = database.driversQueries.hasDrivers().executeAsOne()

    override fun queryVehicles(): List<Vehicle> {
        return database.vehiclesQueries.queryVehicles().executeAsList()
    }

    override fun queryDrivers(): List<Driver> {
        return database.driversQueries.queryDrivers().executeAsList()
    }

    companion object {
        const val TOKEN_KEY = "token_key"
        const val USER_KEY = "user_key"
    }
}
