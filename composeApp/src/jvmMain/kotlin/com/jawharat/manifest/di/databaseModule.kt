package com.jawharat.manifest.di

import app.cash.sqldelight.db.SqlDriver
import com.jawharat.manifest.data.local.adapter.PriceAdapter
import org.koin.dsl.module
import java.io.File

//val databaseModule = module {
//    single<SqlDriver> {
//        val databaseName = "test4.db"
//        val homeDir = System.getProperty("user.home")
//        val appDir = File(homeDir, ".manifest_app")
//
//        if (!appDir.exists()) {
//            appDir.mkdirs()
//        }
//
//        val dbFile = File(appDir, databaseName)
//        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
//
//        if (dbFile.length() == 0L) {
//            ManifestDatabase.Schema.create(driver)
//        }
//
//        driver
//    }
//    single {
//        DispatchRecord.Adapter(priceAdapter = PriceAdapter())
//    }
//    single {
//        ManifestDatabase(driver = get(), DispatchRecordAdapter = get())
//    }
//}
