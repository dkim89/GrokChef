import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

fun getLocalProperty(propertyName: String): String? {
    // Access the root project's file system to find local.properties
    val propertiesFile = project.rootProject.file("local.properties")
    val properties = Properties()

    if (propertiesFile.exists()) {
        // Load properties from the file if it exists
        properties.load(FileInputStream(propertiesFile))
        return properties.getProperty(propertyName)
    } else {
        // If local.properties doesn't exist, return null
        return null
    }
}

kotlin {
    jvmToolchain(17)
}
android {
    namespace = "com.dkapps.grokchef"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.dkapps.grokchef"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Define your API key as a BuildConfigField
        val xaiApiKey = getLocalProperty("xai.api.key")
        if (xaiApiKey != null) {
            buildConfigField("String", "XAI_INITIAL_API_KEY", "\"$xaiApiKey\"")
        } else {
            // Provide a default empty string or throw an error if the key is mandatory
            buildConfigField("String", "XAI_INITIAL_API_KEY", "\"\"")
            println("WARNING: 'xai.api.key' not found in local.properties or environment variables. Initial API key will be empty.")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.material.icons.extended.android)

    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    implementation(libs.retrofit)
    implementation(libs.dagger)
    implementation(libs.converter.gson)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.androidx.navigation.compose)
    ksp(libs.hilt.android.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}