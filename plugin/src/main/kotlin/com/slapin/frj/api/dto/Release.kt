package com.slapin.frj.api.dto

internal data class Release(
  val name: String,
  val displayVersion: String,
  val buildVersion: String,
) {

  val fullVersion: String
    get() = "$displayVersion ($buildVersion)"
}
