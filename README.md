# Firebase Releases Janitor

[![Plugin](https://img.shields.io/maven-metadata/v?label=Gradle%20Plugin&logo=Gradle&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fcom%2Fsergei-lapin%2Ffirebase-releases-janitor%2Fcom.sergei-lapin.firebase-releases-janitor.gradle.plugin%2Fmaven-metadata.xml)](https://plugins.gradle.org/plugin/com.sergei-lapin.firebase-releases-janitor)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Gradle plugin for cleaning up Firebase App Distribution releases

## Prerequisites

In order to use plugin you need
to [create a service account](https://firebase.google.com/docs/app-distribution/authenticate-service-account?platform=android)
and supply its credentials to the plugin.
(by default plugin will attempt to obtain path to credentials file from `GOOGLE_APPLICATION_CREDENTIALS` environment
variable)

## Setup

In you android application module apply plugin

```kotlin
plugins {
    id("com.android.application")
    id("com.sergei-lapin.firebase-releases-janitor") version "{latest-version}"
}
```

if there's `google-services.json` file present — plugin will try to parse it and register tasks for all clients declared
in it.

At this point you can list all registered tasks by
running `./gradlew :your-app-module:tasks --group="Firebase Releases Janitor"`

### Additional clients

If you have clients within your Firebase Project that are not present in `google-services.json` file, you can declare
them like this:

```kotlin
firebaseReleasesJanitor {
    clients.register("otherApp") {
        // required
        applicationId.set("some-id")
    }
}
```

For more info on how to set up the plugin, please refer to `sample/build.gradle.kts`
and `FirebaseReleasesJanitorExtension`'s docs

## Deleting releases

Once you will set up plugin you can delete releases like this

```bash
./gradlew :your-app-module:cleanupSomeClientFirebaseReleases
```

This will remove releases for either `some.client` package name from `google-services.json` or for `someClient` from
clients registered via plugin extension

> **Warning**  
> Task removes all the releases for specified version except for the latest one (by version code)

There's a CLI task that can be used without all the configuration, like this:

```bash
./gradlew :your-app-module:cleanupFirebaseReleases --projectId="project-id" --applicationId="application-id" --releaseVersion="target-version"
```

Also, there's a task that can be used to clean up releases for all registered and parsed clients:

```bash
./gradlew :your-app-module:cleanupAllFirebaseReleases
```

This will just trigger all other tasks, registered for all the clients with the parameters, specified by plugin's
extension