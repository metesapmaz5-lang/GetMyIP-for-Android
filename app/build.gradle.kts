plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

// The release key never enters this repository.
// The password is read from an environment variable during the build only.
val releaseStore = file("${System.getProperty("user.home")}/.plp-keys/getmyip-release.p12")
val releasePassword: String? = System.getenv("GETMYIP_KEY_PASSWORD")

android {
    namespace = "org.parabellumlinuxproject.getmyip"
    compileSdk = 37

    defaultConfig {
        applicationId = "org.parabellumlinuxproject.getmyip"
        minSdk = 26
        targetSdk = 37
        versionCode = 2
        versionName = "1.0.0"
    }

    signingConfigs {
        create("release") {
            storeFile = releaseStore
            storeType = "PKCS12"
            storePassword = releasePassword
            keyAlias = "getmyip"
            keyPassword = releasePassword
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (releaseStore.exists() && releasePassword != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.09.00")
    implementation(composeBom)
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("com.squareup.okhttp3:okhttp:5.5.1")
}
