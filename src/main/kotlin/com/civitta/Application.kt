package com.civitta

import com.civitta.data.remote.Constants
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.websocket.*
import java.time.Duration
import com.civitta.data.remote.models.ServerDateDTO
import com.civitta.data.remote.models.ServerVersionDTO
import com.civitta.domain.models.JVMPlatform
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds

fun main() {
    embeddedServer(Netty, host = "0.0.0.0", port = Constants.SERVER_PORT) {

        // Serializes objects to JSON format
        install(ContentNegotiation) {
            json()
        }

        // Configures web sockets
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
        
        // All server routing
        routing {

            // Simple HTTP request
            get(Constants.Path.GET_VERSION) {
                val platform = JVMPlatform()
                val version = ServerVersionDTO(platform.name, platform.version)
                call.respond(version)
            }

            // Websocket 1:1
            // Sends server Date every 10 sconds or when FE sends input other than "bye", whict will terminate socket
            webSocket(Constants.Path.WS_SERVER_TIME) {
                val scope = CoroutineScope(coroutineContext)

                flow<Unit> {
                    sendSerialized(ServerDateDTO())
                    delay(10.seconds)
                }.launchIn(scope)

                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    if (receivedText.equals("bye", ignoreCase = true)) {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    } else {
                        sendSerialized(ServerDateDTO())
                    }
                }

            }

            // Websocket 1:many
            webSocket(Constants.Path.WS_SERVER_CHAT) {
                // TODO: Make connections
            }
        }
    }
        .start(wait = true)
}

