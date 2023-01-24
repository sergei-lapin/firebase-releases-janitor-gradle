package com.slapin.frj.api.oauth

import com.google.auth.oauth2.GoogleCredentials
import java.io.File

internal fun interface OauthClient {

  fun getAccessToken(serviceAccountFilePath: String): String

  companion object {

    fun default(): OauthClient = OauthClient { serviceAccountFilePath ->
      val credentials =
        GoogleCredentials.fromStream(File(serviceAccountFilePath).inputStream())
          .createScoped("https://www.googleapis.com/auth/cloud-platform")
      credentials.refreshIfExpired()
      val accessToken = credentials.accessToken
      accessToken.tokenValue
    }
  }
}
