package com.jawharat.manifest.domain.exceptions

sealed class NetworkException : Exception() {
    data class TokenExpiredException(override val message: String = "Token has expired") : Exception()
}
