package com.slapin.frj.api.dto

internal data class ReleasesResponseBody(
  val releases: List<Release>,
  val nextPageToken: String?,
)
