package com.jawharat.manifest.utils

enum class Platform {
    Windows, MacOS, Linux
}

val currentPlatform = run {
    val osName = System.getProperty("os.name").lowercase()
    when {
        osName.contains("win") -> Platform.Windows
        osName.contains("mac") -> Platform.MacOS
        else -> Platform.Linux
    }
}