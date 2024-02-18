package com.civitta.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class AppVersionDTO(
    val platform: String,
    val version: String 
)
