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

kotlin {
    targets {
        targetFromPreset(presets.getByName("android"), "androidLib")
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