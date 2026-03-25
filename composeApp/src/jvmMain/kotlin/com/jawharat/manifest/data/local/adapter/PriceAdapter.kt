package com.jawharat.manifest.data.local.adapter

import app.cash.sqldelight.ColumnAdapter

class PriceAdapter: ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long): Int {
        return databaseValue.toInt()
    }

    override fun encode(value: Int): Long {
        return value.toLong()
    }
}