package com.civitta.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class ServerVersionDTO(
    val platform: String,
    val version: String 
)
