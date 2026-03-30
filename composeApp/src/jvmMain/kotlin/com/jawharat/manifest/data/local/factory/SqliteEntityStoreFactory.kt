package com.jawharat.manifest.data.local.factory

import com.jawharat.manifest.db.ManifestDatabase
import kotlin.collections.forEach

interface EntityStore<T> {
    val hasRecords: Boolean
    fun insert(records: List<T>)
    fun query(): List<T>
}

class SqliteEntityStoreFactory(private val db: ManifestDatabase) {
    fun <T> create(
        hasRecords: () -> Boolean,
        insert: (T) -> Unit,
        query: () -> List<T>
    ): EntityStore<T> = object : EntityStore<T> {
        override val hasRecords get() = hasRecords()
        override fun insert(records: List<T>) = db.transaction { records.forEach { insert(it) } }
        override fun query() = query()
    }
}
