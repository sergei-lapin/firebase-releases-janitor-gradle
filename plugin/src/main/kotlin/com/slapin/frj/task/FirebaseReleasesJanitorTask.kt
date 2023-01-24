package com.slapin.frj.task

import com.slapin.frj.api.FirebaseProjectApi
import com.slapin.frj.api.oauth.OauthClient
import com.slapin.frj.domain.filterReleasesForRemoval
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.kotlin.dsl.property
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault
abstract class FirebaseReleasesJanitorTask
@Inject
constructor(
  objectFactory: ObjectFactory,
) : DefaultTask() {

  @Option(
    option = "serviceAccountFilePath",
    description = "Path to file with Firebase Service Account credentials",
  )
  @get:Internal
  val serviceAccountFilePath: Property<String> = objectFactory.property()

  @Option(
    option = "projectId",
    description =
      "Project ID, in which target application is living (project_info.project_number in google-services.json)",
  )
  @get:Internal
  val projectId: Property<String> = objectFactory.property()

  @Option(
    option = "applicationId",
    description =
      "Target application ID (client[].client_info.mobilesdk_app_id in google-services.json)",
  )
  @get:Internal
  val applicationId: Property<String> = objectFactory.property()

  @Option(
    option = "releaseVersion",
    description = "Target version (only latest release of this version will be preserved)",
  )
  @get:Internal
  val releaseVersion: Property<String> = objectFactory.property()

  @TaskAction
  fun cleanupReleases() {
    logger.lifecycle("Getting access token")
    val accessToken =
      OauthClient.default()
        .getAccessToken(
          serviceAccountFilePath = serviceAccountFilePath.get(),
        )
    val api =
      FirebaseProjectApi.default(
        projectId = projectId.get(),
        accessToken = accessToken,
      )
    logger.lifecycle("Getting releases for version ${releaseVersion.get()}")
    val allReleases =
      api.getReleases(
        applicationId = applicationId.get(),
        version = releaseVersion.get(),
      )
    logger.lifecycle("Got ${allReleases.size} release(s)")
    val releasesForRemoval = allReleases.filterReleasesForRemoval()
    if (releasesForRemoval.isNotEmpty()) {
      logger.lifecycle("Deleting ${releasesForRemoval.size} release(s)")
      api.deleteReleases(
        applicationId = applicationId.get(),
        releases = releasesForRemoval,
      )
      val preservedReleases =
        allReleases.filterNot { it in releasesForRemoval }.joinToString { it.fullVersion }
      logger.lifecycle("Done. Preserved $preservedReleases")
    } else {
      logger.lifecycle("No removable releases detected")
    }
  }

  companion object {

    const val Group = "Firebase Releases Janitor"
  }
}
