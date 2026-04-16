package com.jawharat.manifest.domain.exceptions

sealed class NetworkException : Exception() {
    data class TokenExpiredException(override val message: String = "Token has expired") : Exception()
    data class TooManyRequests(override val message: String = "Too many requests") : Exception()
}
