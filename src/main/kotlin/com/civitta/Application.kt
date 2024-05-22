package com.civitta

import com.civitta.data.remote.Constants
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import com.civitta.plugins.configureRouting
import com.civitta.plugins.configureSockets

fun main() {
    embeddedServer(Netty, host = "0.0.0.0", port = Constants.SERVER_PORT) {

        // Serializes objects to JSON format
        install(ContentNegotiation) {
            json()
        }
        
        configureRouting()
        configureSockets()

    }
        .start(wait = true)
}

