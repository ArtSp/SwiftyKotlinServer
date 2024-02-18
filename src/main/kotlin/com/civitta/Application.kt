package com.civitta

import com.civitta.domain.Constants
import com.civitta.plugins.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*

fun main() {
    embeddedServer(Netty, host = "0.0.0.0", port = Constants.SERVER_PORT) {
        install(ContentNegotiation) {
            json()
        }
        
        configureRouting()
    }
        .start(wait = true)
}

