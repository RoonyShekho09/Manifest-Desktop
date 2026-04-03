package com.jawharat.manifest.data.local.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginSessionLocal(
    val token: String,
    val expiresAt: Long
) {
    val isExpired: Boolean get() = System.currentTimeMillis() > expiresAt - 120_000L
}
