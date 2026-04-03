package com.jawharat.manifest.data.local.datasource

import com.jawharat.manifest.data.local.factory.EntityStore
import com.jawharat.manifest.data.local.factory.SqliteEntityStoreFactory
import com.jawharat.manifest.data.local.model.LoginSessionLocal
import com.jawharat.manifest.data.local.model.UserLocal
import com.jawharat.manifest.db.DispatchRecord
import com.jawharat.manifest.db.DriverRecord
import com.jawharat.manifest.db.LineRecord
import com.jawharat.manifest.db.ManifestDatabase
import com.jawharat.manifest.db.VehicleRecord
import com.jawharat.manifest.utils.getObject
import com.jawharat.manifest.utils.putObject


class AppLocalDataSourceImpl(
    private val settings: AppSettings = AppSettings,
    private val database: ManifestDatabase,
    private val sqliteFactory: SqliteEntityStoreFactory
) : AppLocalDataSource {

    override val token: String?
        get() = settings.getObject<LoginSessionLocal>(LOGIN_SESSION_KEY)?.token

    override val hasTokenExpired: Boolean
        get() = settings.getObject<LoginSessionLocal>(LOGIN_SESSION_KEY)?.isExpired == true

    override val lastUsedEmail: String?
        get() = settings.getString(LAST_USED_EMAIL)

    override fun storeLoginSession(value: LoginSessionLocal) =
        settings.putObject<LoginSessionLocal>(LOGIN_SESSION_KEY, value)

    override fun storeLastUsedEmail(value: String) =
        settings.putString(key = LAST_USED_EMAIL, value = value)

    override fun storeUser(value: UserLocal) = settings.putObject(USER_KEY, value)

    override fun clearDataStore() = settings.clear()

    override val drivers: EntityStore<DriverRecord>
        get() =
            sqliteFactory.create(
                hasRecords = { database.driverRecordQueries.hasDrivers().executeAsOne() },
                insert = { driver ->
                    database.driverRecordQueries.insertDriver(
                        id = driver.id,
                        name = driver.name,
                        phone = driver.phone,
                        destination = driver.destination,
                        driverId = driver.driverId
                    )
                },
                query = { database.driverRecordQueries.queryDrivers().executeAsList() }
            )

    override val dispatches: EntityStore<DispatchRecord>
        get() =
            sqliteFactory.create(
                hasRecords = { database.dispatchRecordQueries.hasDispatches().executeAsOne() },
                insert = { dispatch ->
                    database.dispatchRecordQueries.insertDispatch(
                        id = dispatch.id,
                        carType = dispatch.carType,
                        isInside = dispatch.isInside,
                        price = dispatch.price,
                        type = dispatch.type,
                        vehicleNumber = dispatch.vehicleNumber,
                        driver_id = dispatch.driver_id,
                        driver_name = dispatch.driver_name,
                        driver_phone = dispatch.driver_phone,
                        driver_destination = dispatch.driver_destination,
                        office_id = dispatch.office_name,
                        office_name = dispatch.office_name,
                        line_id = dispatch.line_id,
                        line_name = dispatch.line_name,
                    )
                },
                query = { database.dispatchRecordQueries.queryDispatches().executeAsList() }
            )

    override val lines: EntityStore<LineRecord>
        get() =
            sqliteFactory.create(
                hasRecords = { database.lineRecordQueries.hasLines().executeAsOne() },
                insert = { line ->
                    database.lineRecordQueries.insertLine(
                        line.id,
                        line.name,
                    )
                },
                query = { database.lineRecordQueries.queryLines().executeAsList() }
            )

    override val vehicleTypes: EntityStore<VehicleRecord>
        get() =
            sqliteFactory.create(
                hasRecords = { database.vehicleRecordQueries.hasVehicleTypes().executeAsOne() },
                insert = { vehicle ->
                    database.vehicleRecordQueries.insertVehicleType(
                        vehicle.id,
                        vehicle.name,
                    )
                },
                query = { database.vehicleRecordQueries.queryVehicleTypes().executeAsList() }
            )

    companion object {
        const val LOGIN_SESSION_KEY = "login_session_key"
        const val USER_KEY = "user_key"
        const val LAST_USED_EMAIL = "last_used_email_key"
    }
}
