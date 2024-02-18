package com.civitta.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class AppVersionDTO(
    val platform: String,
    val version: String 
)
