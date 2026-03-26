package com.jawharat.manifest.data.local.datasource

import com.jawharat.manifest.data.local.model.vehicles.UserLocal
import com.jawharat.manifest.db.DriverRecord
import com.jawharat.manifest.db.LineRecord
import com.jawharat.manifest.db.ManifestDatabase
import com.jawharat.manifest.db.VehicleRecord
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

    override fun insertDrivers(drivers: List<DriverRecord>) {
        database.transaction {
            drivers.forEach { driver ->
                database.driverRecordQueries.insertDriver(
                    id = driver.id,
                    name = driver.name,
                    phone = driver.phone,
                    destination = driver.destination,
                    driverId = driver.driverId
                )
            }
        }
    }

    override fun insertLines(lines: List<LineRecord>) {
        database.transaction {
            lines.forEach { driver ->
                database.lineRecordQueries.insertLine(
                    id = driver.id,
                    name = driver.name,
                )
            }
        }
    }

    override fun queryLines() = database.lineRecordQueries.queryLines().executeAsList()

    override fun insertVehicles(vehicles: List<VehicleRecord>) {
        database.transaction {
            vehicles.forEach { driver ->
                database.vehicleRecordQueries.insertVehicle(
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
            val hasVehicles = database.vehicleRecordQueries.hasVehicles()
            println("hasVehicles: ${hasVehicles.executeAsList()}")
            hasVehicles.executeAsOne()
        }

    override val hasDriversInDb: Boolean
        get() = database.driverRecordQueries.hasDrivers().executeAsOne()

    override val hasLinesInDb: Boolean
        get() = database.lineRecordQueries.hasLines().executeAsOne()

    override fun queryVehicles(): List<VehicleRecord> =
        database.vehicleRecordQueries.queryVehicles().executeAsList()

    override fun queryDrivers(): List<DriverRecord> =
        database.driverRecordQueries.queryDrivers().executeAsList()


    companion object {
        const val TOKEN_KEY = "token_key"
        const val USER_KEY = "user_key"
    }
}
