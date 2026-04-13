package com.jawharat.manifest.domain

sealed class Exceptions : Exception() {
    data class TokenExpiredException(override val message: String = "Token has expired") : Exception()
}