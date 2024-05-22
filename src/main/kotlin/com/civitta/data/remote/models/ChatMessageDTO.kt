package com.civitta.data.remote.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDTO(
    val sender: String,
    val date: Instant,
    val message: String
)