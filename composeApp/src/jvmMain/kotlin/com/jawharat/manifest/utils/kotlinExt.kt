package com.jawharat.manifest.utils

import com.jawharat.manifest.data.local.datasource.AppSettings
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

inline fun <reified T> AppSettings.putObject(key: String, value: T) {
    putString(key = key, value = value.toJson())
}

inline fun <reified T> AppSettings.getObject(key: String): T? {
    return getString(key = key, default = "").fromJson<T>()
}

inline fun <reified T> String?.fromJson(): T? = runCatching {
    println("this: $this")
    Json.decodeFromString<T>(this ?: "")
}.onFailure {
    println("Failed to convert JsonString to ${T::class.simpleName}")
}.getOrNull()

inline fun <reified T> T?.toJson(): String = runCatching {
    Json.encodeToString(this)
}.onFailure {
    throw SerializationException("Failed to convert ${T::class.simpleName} to JsonString")
}.getOrThrow()

fun String.normalizeArabicKurdish(): String = run {
    replace('ک', 'ك')
    replace('ی', 'ي')
    replace('ی', 'ى')
    replace('ێ', 'ي')
    replace('ە', 'ه')
    replace('م', 'م')
    replace('ح', 'ح')
    replace('ه', 'ة')
}

fun String.containsAny(vararg other: String): Boolean = other.any { contains(it) }