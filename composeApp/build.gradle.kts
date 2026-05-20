import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.ksp)
    alias(libs.plugins.buildConfig)
}

val currentBuild = 2

version = "1.0.0-beta-02"

buildConfig {
    buildConfigField("int", "BUILD_NUMBER", "$currentBuild")
    buildConfigField("APP_VERSION", provider { "\"${project.version}\"" })
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
            implementation("org.jetbrains.compose.ui:ui-test:1.10.0")

            val openCvVersion = "4.9.0-1.5.10"

            implementation("org.bytedeco:opencv:$openCvVersion")

            implementation("org.bytedeco:opencv:$openCvVersion:windows-x86_64")
            implementation("org.bytedeco:openblas:0.3.26-1.5.10:windows-x86_64")
        }
        jvmTest.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.androidx.ui.test.junit4.desktop)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlin.test)
            implementation("io.mockk:mockk:1.14.9")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation("io.sentry:sentry:8.+")
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
        mainClass = "com.jawharat.manifest.MainKt"
        jvmArgs("-Xss2m")

        nativeDistributions {
            modules("java.instrument", "java.management", "jdk.unsupported", "java.sql")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

            packageName = "com.jawharat.manifest"
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
                iconFile.set(project.file("src/jvmMain/composeResources/drawable/logo.ico"))
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
                obfuscate.set(false)
                optimize.set(true)
                joinOutputJars.set(false)
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
