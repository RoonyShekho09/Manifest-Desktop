package com.jawharat.manifest.domain.exceptions

sealed class NetworkException : Exception() {
    data class SessionExpiredException(override val message: String = "Token has expired") : Exception()
    data class ManifestSubmittedRecentlyException(override val message: String = "Too many requests", val retryInSeconds: Int) : Exception()
}
