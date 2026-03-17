# 1. Ignore the duplicate definition warnings
-dontnote **
-dontwarn **


# This allows the build to continue even with duplicate entries
-ignorewarnings

# Keep the META-INF services text files intact
-keeppackagenames kotlinx.coroutines.internal


-keep class kotlinx.coroutines.** { *; }
-keep class kotlin.coroutines.** { *; }
-keep class androidx.compose.** { *; }
-keep class org.jetbrains.compose.** { *; }

-keep class com.your.qr.library.** { *; }
-keepclassmembers class com.your.qr.library.** { *; }


# JVM / launcher bootstrap — without this you get "Failed to launch JVM"
-keep class java.** { *; }
-keep class javax.** { *; }
-keep class sun.** { *; }
-keep class jdk.** { *; }
-keep class com.sun.** { *; }

# Skiko / Skia — Compose Desktop's rendering layer
-keep class org.jetbrains.skia.** { *; }
-keep class org.jetbrains.skiko.** { *; }
-keepclassmembers class org.jetbrains.skiko.** { *; }
