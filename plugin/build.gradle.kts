import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  id("org.jetbrains.kotlin.jvm")
  alias(libs.plugins.dokka)
  alias(libs.plugins.pluginPublish)
  alias(libs.plugins.shadow)
  id("java-gradle-plugin")
}

group = "com.sergei-lapin.firebase-releases-janitor"

version = "1.0.0-alpha01"

kotlin { jvmToolchain(11) }

java {
  withJavadocJar()
  withSourcesJar()
}

tasks.named<Jar>("javadocJar") { from(tasks.named("dokkaJavadoc")) }

tasks.withType<Test> { useJUnitPlatform() }

dependencies {
  shadow(gradleApi())
  shadow(gradleKotlinDsl())

  implementation(libs.googleAuthLib)

  dokkaHtmlPlugin(libs.dokkaKotlinAsJavaPlugin)

  testImplementation(gradleTestKit())
  testImplementation(kotlin("test"))
}

val relocateShadowJar =
  tasks.register<ConfigureShadowRelocation>("relocateShadowJar") {
    target = tasks.getByName<ShadowJar>("shadowJar")
    prefix = "frj.shadow"
  }

tasks.withType<ShadowJar> {
  archiveClassifier.set(null as? String)
  dependsOn(relocateShadowJar)
}

gradlePlugin {
  plugins.create("firebase-releases-janitor") {
    id = group.toString()
    displayName = "Firebase Releases Janitor"
    description = "The Gradle Plugin for cleaning up Firebase App Distribution releases"
    implementationClass = "com.slapin.frj.FirebaseReleasesJanitorPlugin"
  }
}

pluginBundle {
  website = "https://github.com/sergei-lapin/firebase-releases-janitor-gradle"
  vcsUrl = "https://github.com/sergei-lapin/firebase-releases-janitor-gradle.git"
  tags = listOf("distribution", "firebase", "cleanup", "maintenance")
  description = "Plugin that automates distribution to Samsung Galaxy Store"
}
