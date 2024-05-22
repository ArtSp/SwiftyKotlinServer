package com.civitta.data.remote.models.chat

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class MessageDTO(
    val sender: String,
    val date: Instant,
    val message: String
)