package com.jawharat.manifest.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.jawharat.manifest.data.local.adapter.PriceAdapter
import com.jawharat.manifest.db.ManifestDatabase
import com.jawharat.manifest.db.Vehicle
import org.koin.dsl.module
import java.util.Properties

val databaseModule = module {
    single<SqlDriver> {
        JdbcSqliteDriver(
            "jdbc:sqlite:test.db",
            Properties(),
            ManifestDatabase.Schema
        )
    }
    single {
        ManifestDatabase(driver = get(), Vehicle.Adapter(priceAdapter = PriceAdapter()))
    }
}
