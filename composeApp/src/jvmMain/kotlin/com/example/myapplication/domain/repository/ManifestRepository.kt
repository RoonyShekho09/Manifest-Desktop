package com.example.myapplication.domain.repository

import com.example.myapplication.domain.entity.Driver
import com.example.myapplication.domain.entity.Vehicle

interface ManifestRepository {
    suspend fun getDrivers(): List<Driver>
    suspend fun getVehicles(): List<Vehicle>
}
