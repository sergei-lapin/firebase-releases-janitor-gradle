@file:Suppress("UnstableApiUsage")

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.sergei-lapin.firebase-releases-janitor") version "1.0.0-alpha01"
}

firebaseReleasesJanitor {
  projectId.set("123456789012")
  // all properties can be set via providers, like this one
  releaseVersion.set(providers.environmentVariable("RELEASE_VERSION"))

  clients.register("testApp") {
    // required
    applicationId.set("1:123456789013:android:1234567890123456789012")
    // this will override general projectId
    projectId.set("123456789013")
    // this will override general version
    releaseVersion.set("2.0.0")
  }
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
  implementation("androidx.appcompat:appcompat:1.6.0")
  implementation("com.google.android.material:material:1.7.0")
}
