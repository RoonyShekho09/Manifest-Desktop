package com.jawharat.manifest.data.remote.exceptions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TooManyRequestsException(
    @SerialName("error")
    val error: String? = null,
    @SerialName("existingManifestCreatedAt")
    val existingManifestCreatedAt: String? = null,
    @SerialName("existingManifestId")
    val existingManifestId: String? = null,
    @SerialName("retryAfter")
    val retryAfter: Int? = null,
    @SerialName("unlockTime")
    val unlockTime: String? = null
): Exception()