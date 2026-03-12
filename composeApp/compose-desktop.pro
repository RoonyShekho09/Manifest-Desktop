# 1. Ignore the duplicate definition warnings
-dontnote **
-dontwarn **

# 2. Specifically address the Bytedeco/JavaCPP duplicates you see in the log
-dontnote org.bytedeco.**

# 3. Allow ProGuard to continue even if class names don't match folder structures perfectly
# (This fixes the "IOException: Please correct the above warnings first")


# Ignore the specific ByteDeco/OpenCV warnings
-dontnote org.bytedeco.**
-dontwarn org.bytedeco.**

# This allows the build to continue even with duplicate entries
-ignorewarnings

# Keep the META-INF services text files intact
-keeppackagenames kotlinx.coroutines.internal


-keep class kotlinx.coroutines.** { *; }
-keep class kotlin.coroutines.** { *; }
-keep class androidx.compose.** { *; }
-keep class org.jetbrains.compose.** { *; }

-keep class org.bytedeco.** { *; }
-keepclassmembers class org.bytedeco.** { *; }
-keep class * implements org.bytedeco.javacpp.** { *; }
-keep class * extends org.bytedeco.javacpp.** { *; }
-keepnames class org.bytedeco.**

-keep class com.your.qr.library.** { *; }
-keepclassmembers class com.your.qr.library.** { *; }
