import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.ktfmt)
    alias(libs.plugins.kotlin.serialization)
}

android.buildFeatures.buildConfig = true

android {
    namespace = "kzs.th000.curioushub"
    compileSdk = 36

    defaultConfig {
        applicationId = "kzs.th000.curioushub"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    room { schemaDirectory("$projectDir/schemas") }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            val clientProperties = Properties()
            clientProperties.load(project.rootProject.file("/app/client_dev.properties").reader())
            val clientId = clientProperties.getProperty("ClientId")
            val clientSecret = clientProperties.getProperty("ClientSecret")
            buildConfigField("String", "CLIENT_ID", clientId)
            buildConfigField("String", "CLIENT_SECRET", clientSecret)
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            val clientProperties = Properties()
            clientProperties.load(project.rootProject.file("/app/client.properties").reader())
            val clientId = clientProperties.getProperty("ClientId")
            val clientSecret = clientProperties.getProperty("ClientSecret")
            buildConfigField("String", "CLIENT_ID", clientId)
            buildConfigField("String", "CLIENT_SECRET", clientSecret)
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
    buildFeatures { compose = true }
}

ktfmt { kotlinLangStyle() }

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.ktor.core)
    implementation(libs.ktor.okhttp)
    implementation(libs.nativation.compose)
    implementation(libs.ktx.ser.json)
    implementation(libs.datastore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    ksp(libs.room.compiler)
}
