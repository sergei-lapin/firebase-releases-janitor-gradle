package com.slapin.frj.domain

import com.slapin.frj.api.dto.Release
import java.util.*
import kotlin.random.Random
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ReleasesForRemovalTest {

  @Test
  fun `test zero releases`() {
    val result = emptyList<Release>().filterReleasesForRemoval()
    assertTrue(result.isEmpty())
  }

  @Test
  fun `test single release`() {
    val result = createReleasesList(size = 1).filterReleasesForRemoval()
    assertTrue(result.isEmpty())
  }

  @Test
  fun `multiple releases`() {
    val releases = createReleasesList(size = Random.nextInt(2, 200))
    val preservedRelease = releases.maxBy { it.buildVersion.toInt() }
    val releasesForRemoval =
      releases.filter { it != preservedRelease }.sortedByDescending { it.buildVersion }
    val result = releases.filterReleasesForRemoval()
    assertTrue(preservedRelease !in result)
    assertEquals(releasesForRemoval, result)
  }

  private fun createReleasesList(
    size: Int,
  ): List<Release> {
    val offset = Random.nextInt(1, 25000)
    return List(size) { index ->
        Release(
          name = UUID.randomUUID().toString(),
          displayVersion = "1.0.0",
          buildVersion = (offset + index).toString(),
        )
      }
      .shuffled()
  }
}
