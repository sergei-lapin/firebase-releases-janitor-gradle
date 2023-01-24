package com.slapin.frj.domain

import com.slapin.frj.api.dto.Release

internal fun List<Release>.filterReleasesForRemoval(): List<Release> {
  if (isEmpty() || size == 1) return emptyList()
  return sortedByDescending(Release::buildVersion).drop(1)
}
