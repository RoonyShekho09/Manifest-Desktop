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

ktorfit {
    compilerPluginVersion.set("2.3.3")
}

compose.desktop {
    application {
        mainClass = "com.example.myapplication.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.example.myapplication"
            packageVersion = "1.0.0"
            macOS {
                iconFile.set(project.file("drawable/ic_jawharat.xml"))
            }

            windows {
                iconFile.set(project.file("drawable/ic_jawharat.xml"))
            }

            linux {
                iconFile.set(project.file("drawable/ic_jawharat.xml"))
            }
        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "me.sample.library.resources"
    generateResClass = auto
}
