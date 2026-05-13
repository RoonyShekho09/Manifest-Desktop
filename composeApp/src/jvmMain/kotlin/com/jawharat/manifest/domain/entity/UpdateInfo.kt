package com.jawharat.manifest.domain.entity

data class UpdateInfo(
    val latestBuild: Int,
    val isForced: Boolean,
    val minBuild: Int
)