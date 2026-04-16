package com.jawharat.manifest.domain.entity

data class UpdateInfo(
    val build: Int,
    val isForced: Boolean,
    val minBuild: Int
)