repositories {
    google()
    mavenCentral()  // Ensure Maven Central is included
}

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    //alias(libs.plugins.androidApplication) apply false
    //alias(libs.plugins.androidLibrary) apply false
    //alias(libs.plugins.composeMultiplatform) apply false
    // alias(libs.plugins.composeCompiler) apply false
  //  alias(libs.plugins.kotlinMultiplatform) apply false

    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false

    alias(libs.plugins.google.gms.google.services) apply false


}
