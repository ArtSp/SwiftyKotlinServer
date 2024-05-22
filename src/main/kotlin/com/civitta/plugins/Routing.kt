package com.civitta.plugins

import io.ktor.server.application.*
import com.civitta.data.remote.Constants
import com.civitta.data.remote.models.ServerVersionDTO
import com.civitta.domain.models.JVMPlatform
import io.ktor.server.response.*
import io.ktor.server.routing.*
fun Application.configureRouting() {
    routing {
        // Simple HTTP request
        get(Constants.Path.GET_VERSION) {
            val platform = JVMPlatform()
            val version = ServerVersionDTO(platform.name, platform.version)
            call.respond(version)
        }
    }
}