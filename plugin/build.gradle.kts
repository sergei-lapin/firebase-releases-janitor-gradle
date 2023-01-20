import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  alias(libs.plugins.kotlin)
  alias(libs.plugins.dokka)
  alias(libs.plugins.pluginPublish)
  id("java-gradle-plugin")
}

group = "com.sergei-lapin.firebase-releases-janitor"

version = "1.0.0-alpha01"

val jvmTarget = JavaVersion.VERSION_11.toString()

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = jvmTarget }

tasks.withType<JavaCompile> {
  sourceCompatibility = jvmTarget
  targetCompatibility = jvmTarget
}

java {
  withJavadocJar()
  withSourcesJar()
}

tasks.named<Jar>("javadocJar") { from(tasks.named("dokkaJavadoc")) }

dependencies {
  compileOnly(gradleApi())
  compileOnly(gradleKotlinDsl())
  compileOnly(libs.agp)

  implementation(libs.jwt)
  implementation(libs.gson)
  implementation(libs.okhttp)

  dokkaHtmlPlugin(libs.dokkaKotlinAsJavaPlugin)

  testImplementation(gradleTestKit())
}

gradlePlugin {
  plugins.create("firebase-releases-janitor") {
    id = group.toString()
    displayName = "Firebase Releases Janitor"
    description = "The Gradle Plugin for cleaning up dangling Firebase App Distribution releases"
    implementationClass = "com.slapin.frj.FirebaseReleasesJanitorPlugin"
  }
}

pluginBundle {
  website = "https://github.com/sergei-lapin/firebase-releases-janitor-gradle"
  vcsUrl = "https://github.com/sergei-lapin/firebase-releases-janitor-gradle.git"
  tags = listOf("distribution", "firebase", "cleanup", "maintenance")
  description = "Plugin that automates distribution to Samsung Galaxy Store"
}
