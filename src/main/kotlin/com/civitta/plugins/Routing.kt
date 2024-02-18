package com.civitta.plugins

import com.civitta.domain.Constants
import com.civitta.domain.models.AppVersionDTO
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get(Constants.Path.GET_VERSION) {
            val version = AppVersionDTO("platform.name", "platform.version")
            call.respond(version)
        }
    }
}
