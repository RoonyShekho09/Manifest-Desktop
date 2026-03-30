package com.jawharat.manifest.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.jawharat.manifest.data.local.adapter.PriceAdapter
import com.jawharat.manifest.db.DispatchRecord
import com.jawharat.manifest.db.ManifestDatabase
import org.koin.dsl.module
import java.util.Properties

val databaseModule = module {
    single<SqlDriver> {
        JdbcSqliteDriver(
            "jdbc:sqlite:test4.db",
            Properties(),
            ManifestDatabase.Schema
        )
    }
    single {
        ManifestDatabase(driver = get(), DispatchRecord.Adapter(priceAdapter = PriceAdapter()))
    }
}
