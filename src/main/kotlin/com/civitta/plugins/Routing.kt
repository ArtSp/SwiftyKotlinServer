package com.civitta.plugins

import com.civitta.data.remote.Constants
import com.civitta.data.remote.models.AppVersionDTO
import com.civitta.domain.models.JVMPlatform
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get(Constants.Path.GET_VERSION) {
            val platform = JVMPlatform()
            val version = AppVersionDTO(platform.name, platform.version)
            call.respond(version)
        }
    }
}
