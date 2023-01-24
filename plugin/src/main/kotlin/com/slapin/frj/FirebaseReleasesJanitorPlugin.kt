package com.slapin.frj

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.slapin.frj.domain.GoogleServicesProjectDescription
import com.slapin.frj.task.FirebaseReleasesJanitorTask
import java.util.*
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

abstract class FirebaseReleasesJanitorPlugin : Plugin<Project> {

  override fun apply(target: Project) =
    with(target) {
      val globalTask = registerGlobalTask()
      val extension = createExtension()
      registerCLITask(
        extension = extension,
      )
      registerTasksForIncomingClients(
        extension = extension,
        globalTask = globalTask,
      )
      maybeRegisterTasksForGoogleServicesClientEntries(
        extension = extension,
        globalTask = globalTask,
      )
    }

  private fun Project.registerGlobalTask(): TaskProvider<DefaultTask> {
    return tasks.register<DefaultTask>("cleanupAllFirebaseReleases") {
      group = FirebaseReleasesJanitorTask.Group
      description = "Task for deleting redundant releases for all registered and parsed clients"
    }
  }

  private fun Project.createExtension(): FirebaseReleasesJanitorExtension {
    return extensions.create("firebaseReleasesJanitor", objects, providers)
  }

  private fun Project.registerCLITask(
    extension: FirebaseReleasesJanitorExtension,
  ) {
    tasks.register<FirebaseReleasesJanitorTask>("cleanupFirebaseReleases") {
      serviceAccountFilePath.set(extension.serviceAccountFilePath)
      group = FirebaseReleasesJanitorTask.Group
      description = "Task for deleting releases via CLI (specifying task inputs via options)"
    }
  }

  private fun Project.registerTasksForIncomingClients(
    extension: FirebaseReleasesJanitorExtension,
    globalTask: TaskProvider<*>,
  ) {
    extension.clients.whenObjectAdded { client ->
      val clientCleanupTaskProvider =
        tasks.register<FirebaseReleasesJanitorTask>(
          "cleanup${client.name.capitalize()}FirebaseReleases"
        ) {
          projectId.set(client.projectId.orElse(extension.projectId))
          applicationId.set(client.applicationId)
          releaseVersion.set(client.releaseVersion.orElse(extension.releaseVersion))
          serviceAccountFilePath.set(extension.serviceAccountFilePath)
          group = FirebaseReleasesJanitorTask.Group
          description = "Task for deleting redundant releases of ${client.name}"
        }
      globalTask.dependsOn(clientCleanupTaskProvider)
    }
  }

  private fun Project.maybeRegisterTasksForGoogleServicesClientEntries(
    extension: FirebaseReleasesJanitorExtension,
    globalTask: TaskProvider<*>,
  ) {
    val googleServicesFile = layout.projectDirectory.file("google-services.json").asFile
    if (googleServicesFile.exists()) {
      try {
        val projectDescription =
          GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
            .fromJson(googleServicesFile.reader(), GoogleServicesProjectDescription::class.java)
        projectDescription.client
          .map { it.clientInfo }
          .forEach { clientInfo ->
            val packageName = clientInfo.androidClientInfo.packageName
            val clientNameCapitalized =
              packageName.split('.').joinToString("") { word -> word.lowercase().capitalize() }
            val clientCleanupTaskProvider =
              tasks.register<FirebaseReleasesJanitorTask>(
                "cleanup${clientNameCapitalized}FirebaseReleases"
              ) {
                projectId.set(projectDescription.projectInfo.projectNumber)
                applicationId.set(clientInfo.mobilesdkAppId)
                releaseVersion.set(extension.releaseVersion)
                serviceAccountFilePath.set(extension.serviceAccountFilePath)
                group = FirebaseReleasesJanitorTask.Group
                description = "Task for deleting redundant releases of $packageName"
              }
            globalTask.dependsOn(clientCleanupTaskProvider)
          }
      } catch (e: JsonIOException) {
        logger.warn(e.message, e)
      } catch (e: JsonSyntaxException) {
        logger.warn(e.message, e)
      } catch (e: NullPointerException) {
        logger.warn("Some field didn't parse as expected", e)
      }
    }
  }

  private fun TaskProvider<*>.dependsOn(other: TaskProvider<*>) {
    configure { it.dependsOn(other) }
  }

  private fun String.capitalize(): String = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
  }
}
