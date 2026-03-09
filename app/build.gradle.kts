plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)

}

android {
    namespace = "com.unesexact.friendaccountingapp"
    compileSdk {
        version = release(36)
        composeOptions {
            kotlinCompilerExtensionVersion = "1.6.10"
        }
    }

    defaultConfig {
        applicationId = "com.unesexact.friendaccountingapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    // Core Android
    implementation(libs.androidx.core.ktx)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Compose UI
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.runtime.saveable)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Material Components (required for XML theme)
    implementation(libs.material)

    // Debug tools
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Unit tests
    testImplementation(libs.junit)

    // Android instrumentation tests
    androidTestImplementation(libs.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core.v351)

    // Compose UI testing
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Room testing
    androidTestImplementation(libs.androidx.room.testing.v261)

    // Coroutines testing
    androidTestImplementation(libs.kotlinx.coroutines.test.v173)

    // Truth assertions
    androidTestImplementation(libs.truth.v115)

    // Architecture testing
    androidTestImplementation(libs.androidx.core.testing)
}


