package com.slapin.frj.api

import com.google.gson.Gson
import com.slapin.frj.api.dto.BatchDeleteRequestBody
import com.slapin.frj.api.dto.Release
import com.slapin.frj.api.dto.ReleasesResponseBody
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

internal interface FirebaseProjectApi {

  val projectId: String
  val accessToken: String

  fun getReleases(
    applicationId: String,
    version: String,
  ): List<Release>

  fun deleteReleases(
    applicationId: String,
    releases: List<Release>,
  )

  companion object {

    fun default(
      projectId: String,
      accessToken: String,
    ): FirebaseProjectApi =
      FirebaseProjectApiImpl(
        projectId = projectId,
        accessToken = accessToken,
      )
  }
}

internal class FirebaseProjectApiImpl(
  override val projectId: String,
  override val accessToken: String,
) : FirebaseProjectApi {

  private val httpClient = HttpClient.newHttpClient()

  override fun getReleases(
    applicationId: String,
    version: String,
  ): List<Release> {
    var page = 0
    var nextPageToken: String? = null
    val result = mutableListOf<Release>()
    val gson = Gson()
    do {
      page++
      val releasesRequest =
        buildReleasesRequest(
          applicationId = applicationId,
          version = version,
          pageToken = nextPageToken,
        )
      val releasesResponse = httpClient.send(releasesRequest, HttpResponse.BodyHandlers.ofString())
      val releasesResponseParsed =
        gson.fromJson(releasesResponse.body(), ReleasesResponseBody::class.java)
      result.addAll(releasesResponseParsed.releases)
      nextPageToken = releasesResponseParsed.nextPageToken
    } while (nextPageToken != null)
    return result
  }

  private fun buildReleasesRequest(
    applicationId: String,
    version: String,
    pageToken: String?,
  ): HttpRequest {
    val requestUri =
      buildUri(
        projectId = projectId,
        applicationId = applicationId,
      ) {
        method = "releases"
        queryParameter("pageSize", "25")
        queryParameter("query", version)
        pageToken?.let { queryParameter("pageToken", pageToken) }
      }
    return HttpRequest.newBuilder()
      .uri(requestUri)
      .header("Accept", "application/json")
      .header("Authorization", "Bearer $accessToken")
      .GET()
      .build()
  }

  override fun deleteReleases(
    applicationId: String,
    releases: List<Release>,
  ) {
    val requestBody = Gson().toJson(BatchDeleteRequestBody(names = releases.map { it.name }))
    val requestUri =
      buildUri(
        projectId = projectId,
        applicationId = applicationId,
      ) {
        method = "releases:batchDelete"
      }
    val batchDeleteRequest =
      HttpRequest.newBuilder()
        .uri(requestUri)
        .header("Accept", "application/json")
        .header("Authorization", "Bearer $accessToken")
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build()
    httpClient.send(batchDeleteRequest, HttpResponse.BodyHandlers.discarding())
  }
}
