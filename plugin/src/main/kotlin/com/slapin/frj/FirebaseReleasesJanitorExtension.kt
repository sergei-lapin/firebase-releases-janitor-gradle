package com.slapin.frj

import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.property

abstract class FirebaseReleasesJanitorExtension(
  objects: ObjectFactory,
  providers: ProviderFactory,
) {

  /**
   * The path to the service account file used to authenticate with the Firebase App Distribution
   * API. This file should contain the private key and other credentials required to authenticate
   * with the API. The service account should have the necessary permissions to call the App
   * Distribution API.
   */
  val serviceAccountFilePath: Property<String> =
    objects
      .property<String>()
      .convention(providers.environmentVariable("GOOGLE_APPLICATION_CREDENTIALS"))

  /**
   * The unique identifier for the Firebase project. Can be found in the google-services.json file,
   * in the "project_info" object, under the "project_number" field.
   */
  abstract val projectId: Property<String>

  /**
   * The target version of the release that should be cleaned up. Only the latest release of this
   * version will be kept, and all other releases of this version will be deleted upon task
   * execution.
   */
  abstract val releaseVersion: Property<String>

  /**
   * A container for holding additional clients for the Firebase project, beyond those parsed from
   * the google-services.json file. Allows for the easy management and configuration of additional
   * clients for the project.
   */
  abstract val clients: NamedDomainObjectContainer<FirebaseProjectApplicationClient>
}

interface FirebaseProjectApplicationClient : Named {

  /**
   * The unique identifier for the Firebase application within the project. Can be found in the
   * google-services.json file, in the "client" array, under the "client_info" object, under the
   * "mobilesdk_app_id" field.
   */
  val applicationId: Property<String>

  /** Same as [FirebaseReleasesJanitorExtension.projectId], but, if set, will override it */
  val projectId: Property<String>

  /** Same as [FirebaseReleasesJanitorExtension.releaseVersion], but, if set, will override it */
  val releaseVersion: Property<String>
}
