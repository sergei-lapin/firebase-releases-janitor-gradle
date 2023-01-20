@file:Suppress("UnstableApiUsage")

plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.kotlinAndroid)
  id("com.sergei-lapin.firebase-releases-janitor")
}

android {
  compileSdk = 33
  namespace = "com.slapin.gsp.sample"

  defaultConfig {
    applicationId = "com.slapin.gsp.sample"
    minSdk = 21
    targetSdk = 33
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  val jvmTarget = JavaVersion.VERSION_11

  compileOptions {
    sourceCompatibility = jvmTarget
    targetCompatibility = jvmTarget
  }

  kotlinOptions.jvmTarget = jvmTarget.toString()
}

dependencies {
  implementation("androidx.appcompat:appcompat:1.5.1")
  implementation("com.google.android.material:material:1.6.1")
}
