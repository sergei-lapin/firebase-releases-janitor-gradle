package com.slapin.frj.domain

internal data class GoogleServicesProjectDescription(
  val projectInfo: ProjectInfo,
  val client: List<Client>,
) {

  data class ProjectInfo(
    val projectNumber: String,
  )

  data class Client(
    val clientInfo: ClientInfo,
  ) {

    data class ClientInfo(
      val mobilesdkAppId: String,
      val androidClientInfo: AndroidClientInfo,
    ) {

      data class AndroidClientInfo(
        val packageName: String,
      )
    }
  }
}
