package com.civitta.data.remote.models.chat

import kotlinx.serialization.Serializable

@Serializable
data class MessageStatusDTO(
    val sender: UserDTO?,
    val isTyping: Boolean
)