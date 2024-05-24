package com.civitta.data.remote.models.chat

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: String,
    val name: String,
    val os: OS
) {
    enum class OS { ANDROID, IOS }
}