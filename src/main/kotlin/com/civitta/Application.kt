package com.civitta

import com.civitta.data.remote.Constants
import com.civitta.data.remote.plugins.configureSockets
import com.civitta.data.remote.plugins.configureRouting
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*

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

