

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)

     alias(libs.plugins.ksp)
     alias(libs.plugins.room)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose) // includes viewModel support
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
           // implementation(libs.koin.android)
            //implementation(libs.koin.androidx.compose) // includes viewModel support

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.dateTimePicker2)
            implementation(libs.dateTimePicker)

            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)


        }
        iosMain.dependencies{
            implementation(libs.ktor.client.darwin)



        }
    }
}

android {

    namespace = "com.tomiappdevelopment.milk_flow"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.tomiappdevelopment.milk_flow"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    debugImplementation(compose.uiTooling)
    implementation(libs.ktor.client.android)
   // implementation(libs.koin.android)
 //   implementation(libs.koin.androidx.compose)

    ksp(libs.room.compiler)


}




