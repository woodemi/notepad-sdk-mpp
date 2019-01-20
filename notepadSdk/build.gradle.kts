import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("kotlin-multiplatform").version("1.3.30")
    id("com.android.library").version("3.2.0")
}

repositories {
    google()
    jcenter()
}

group = "io.woodemi"
version = "0.0.1"

// Normally [RELEASE, DEBUG]
val iOSBuildType = project.properties["kotlin.build.type"] as? String ?: "DEBUG"
val embedBitcodMode = if (iOSBuildType == "RELEASE") Framework.BitcodeEmbeddingMode.BITCODE else Framework.BitcodeEmbeddingMode.MARKER
// [iosX64, iosArm64, iosArm32]
val iOSTargetPreset = project.properties["kotlin.target"] as? String ?: "iosX64"

kotlin {
    targets {
        targetFromPreset(presets.getByName("android"), "androidLib")

        targetFromPreset(presets.getByName(iOSTargetPreset), "ios") {
            (this as KotlinNativeTarget).binaries.framework {
                embedBitcode(embedBitcodMode)
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
            }
        }
        val androidLibMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib")
            }
        }
        val iosMain by getting {}
    }
}

android {
    compileSdkVersion(28)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "0.0.1"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        val release by getting {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    // TODO AndroidLib dependencies
}

// This task attaches native framework built from ios module to Xcode project
// (see iosApp directory). Don't run this task directly,
// Xcode runs this task itself during its build process.
// Before opening the project from iosApp directory in Xcode,
// make sure all Gradle infrastructure exists (gradle.wrapper, gradlew).
tasks.create("copyFramework") {
    val nativeTarget = kotlin.targets["ios"] as KotlinNativeTarget
    val nativeBinary = nativeTarget.binaries.getFramework(iOSBuildType)
    dependsOn(nativeBinary.linkTask)

    doLast {
        val targetDir = project.property("configuration.build.dir") as? String ?: "."
        copy {
            from(nativeBinary.outputFile.parent)
            into(targetDir)
            include("NotepadKit.framework/**")
            include("NotepadKit.framework.dSYM")
        }
    }
}