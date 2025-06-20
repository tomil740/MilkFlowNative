

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)

    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.google.gms.google.services)

}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            linkerOpts("-framework", "Network")
        }
    }
    
    sourceSets {
        
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose) // includes viewModel support
            implementation(libs.koin.android.workmanager) // includes viewModel support


        }
        commonMain.dependencies {
              implementation(compose.runtime)
              implementation(compose.foundation)
              implementation(compose.ui)
              implementation(compose.components.resources)
              implementation(compose.components.uiToolingPreview)


            implementation(libs.androidx.material3)
            implementation(libs.navigator)
            implementation(libs.navigator.screen.model)
            implementation(libs.navigator.transitions)
            implementation(libs.navigator.koin)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            implementation(libs.bundles.ktor)


            implementation(libs.dateTimePicker2)
            implementation(libs.dateTimePicker)

            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            implementation(libs.gitlive.firestore)

            implementation(libs.kotlinx.serialization.json)

            implementation(libs.multiplatform.settings)


        }
        iosMain.dependencies{
            implementation(libs.ktor.client.darwin)

        }
    }
}

android {

    namespace = "com.tomiappdevelopment.milk_flow"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.tomiappdevelopment.milk_flow"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"


      //  buildConfigField("String", "FIREBASE_API_KEY", AIzaSyDbmxhBoZHY0H5MJSzz5idhrC6FbFQSSI)
        buildConfigField("String", "FIREBASE_API_KEY", "\"${"AIzaSyATeHZfcXHubUpHmwmnLJmUQ-NsVb7bGSk"}\"")



    }

}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    debugImplementation(compose.uiTooling)
    implementation(libs.ktor.client.okhttp)

    ksp(libs.room.compiler)

}




