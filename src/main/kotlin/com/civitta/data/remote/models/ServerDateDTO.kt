package com.civitta.data.remote.models

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ServerDateDTO(
    var date: Instant = Clock.System.now()
)