import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.ksp)
}

kotlin {
    jvm()

    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.bundles.utils)
            implementation(libs.bundles.compose)
            implementation(libs.bundles.koin)
            implementation(libs.bundles.network)
            implementation(libs.kotlinx.serializaion)
            implementation("com.google.zxing:javase:3.5.3")
            implementation("com.github.sarxos:webcam-capture:0.3.12")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(fileTree("src/jvmMain/java") { include("*.jar") })
        }
    }
}

dependencies {
    add("kspJvm", libs.ktorfit.ksp)
}

ktorfit {
    compilerPluginVersion.set("2.3.3")
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    exclude("META-INF/MANIFEST.MF")
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
}

compose.desktop {
    application {
        mainClass = "com.example.myapplication.MainKt"
        jvmArgs("-Xss2m")

        nativeDistributions {
            modules("java.instrument", "java.management", "jdk.unsupported")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

            packageName = "com.example.myapplication"
            packageVersion = "1.0.0"

            macOS {
                iconFile.set(project.file("src/jvmMain/composeResources/drawable/ic_jawharat.png"))
                packageName = "Manifest"

                infoPlist {
                    extraKeysRawXml = """
            <key>NSCameraUsageDescription</key>
            <string>Camera access is required for QR code scanning</string>
        """
                }
            }

            windows {
                iconFile.set(project.file("src/jvmMain/composeResources/drawable/ic_jawharat.png"))
                packageName = "Manifest"
                menu = true
                shortcut = true
                dirChooser = true
            }

            linux {
                iconFile.set(project.file("src/jvmMain/composeResources/drawable/ic_jawharat.png"))
                packageName = "Manifest"
            }

            buildTypes.release.proguard {
                isEnabled.set(false)
                obfuscate.set(true)
                optimize.set(true)
                joinOutputJars.set(true)
                configurationFiles.from(project.file("compose-desktop.pro"))
            }
        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "com.jawharat.manifest.resources"
    generateResClass = auto
}

tasks.register("analyzeDependencies") {
    doLast {
        val config = configurations.getByName("jvmRuntimeClasspath")
        config.resolvedConfiguration.resolvedArtifacts
            .sortedByDescending { it.file.length() }
            .take(20)
            .forEach {
                val sizeKb = it.file.length() / 1024
                println("${sizeKb}KB — ${it.moduleVersion.id}:${it.classifier ?: ""}")
            }
    }
}
