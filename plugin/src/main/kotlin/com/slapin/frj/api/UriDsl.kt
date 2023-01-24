package com.slapin.frj.api

import java.net.URI

internal fun buildUri(builder: UriBuilder.() -> Unit): URI {
  return URI(UriBuilder().apply(builder).toString())
}

internal fun buildUri(
  projectId: String,
  applicationId: String,
  builder: UriBuilder.() -> Unit,
): URI {
  return buildUri {
    this.projectId = projectId
    this.applicationId = applicationId
    builder.invoke(this)
  }
}

private const val BaseUrl = "https://firebaseappdistribution.googleapis.com/v1"

internal class UriBuilder {

  var projectId: String? = null
  var applicationId: String? = null
  var method: String? = null

  private val queryParameters: MutableMap<String, String> = mutableMapOf()

  fun queryParameter(name: String, value: String) {
    queryParameters[name] = value
  }

  override fun toString(): String {
    val projectId = requireNotNull(projectId) { "projectId wasn't set" }
    val applicationId = requireNotNull(applicationId) { "applicationId wasn't set" }
    val method = requireNotNull(method) { "method wasn't set" }
    return buildString {
      append(BaseUrl)
      append("/projects")
      append("/$projectId")
      append("/apps")
      append("/$applicationId")
      append("/$method")
      if (queryParameters.isNotEmpty()) {
        append('?')
        append(queryParameters.map { (key, value) -> "$key=$value" }.joinToString("&"))
      }
    }
  }
}
