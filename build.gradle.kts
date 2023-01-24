plugins {
  val kotlinVersion = "1.8.0"
  id("org.jetbrains.kotlin.jvm") version kotlinVersion apply false
  id("org.jetbrains.kotlin.android") version kotlinVersion apply false
  id("com.android.application") version "7.3.1" apply false
}