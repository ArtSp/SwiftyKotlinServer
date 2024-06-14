package com.civitta.data.remote.plugins

import io.ktor.server.application.*
import com.civitta.data.remote.Constants
import io.ktor.server.websocket.*
import com.civitta.data.remote.models.ServerDateDTO
import com.civitta.data.remote.models.SocketConnection
import com.civitta.data.remote.models.chat.*
import com.civitta.domain.models.JVMPlatform
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds
import java.time.Duration
import java.util.Collections
import kotlin.collections.LinkedHashSet

fun Application.configureSockets() {
    
    // Configures web sockets
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
    
    routing {
        serverTimeSocket()
        chatSocket()
    }
}

// Websocket 1:1
// Sends server Date every 10 seconds or when FE sends input other than "bye", which will terminate socket
private fun Routing.serverTimeSocket() {
    webSocket(Constants.Path.WS_SERVER_TIME) {
        val scope = CoroutineScope(coroutineContext)
        val platform = JVMPlatform()

        flow<Unit> {
            while (true) {
                sendSerialized(ServerDateDTO(source = platform.name))
                delay(1.seconds)
            }
        }.launchIn(scope)

        for (frame in incoming) {
            frame as? Frame.Text ?: continue
            val receivedText = frame.readText()
            if (receivedText.equals("bye", ignoreCase = true)) {
                close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
            } else {
                sendSerialized(ServerDateDTO(source = platform.name))
            }
        }

    }
}

// Websocket 1:many
// Multicast server message to all listeners "bye" will terminate socket.
private fun Routing.chatSocket() {
    val connections = Collections.synchronizedSet<SocketConnection?>(LinkedHashSet())
    webSocket(Constants.Path.WS_SERVER_CHAT) {
        // Save connection
        val thisConnection = SocketConnection(this)
        connections += thisConnection
        
        // Start multicast
        try {
            sendChatMemberCount(connections)
            for (frame in incoming) {
                try {
                    converter?.deserialize<UserConnectionDTO>(frame)?.let {
                        println("User ${it.name} joined chat")
                        val os = if (call.request.header("os")?.lowercase() == "ios") UserDTO.OS.IOS else UserDTO.OS.ANDROID
                        val user = UserDTO(thisConnection.sessionID, it.name, os)
                        sendSerialized(user)
                    }
                } catch(_: Throwable) {  }

                try {
                    converter?.deserialize<MessageDTO>(frame)?.let { multicast(connections, it) }
                } catch(_: Throwable) {  }

                try {
                    converter?.deserialize<MessageStatusDTO>(frame)?.let { multicast(connections, it) }
                } catch(_: Throwable) {  }
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        } finally {
            println("Removing $thisConnection!")
            connections -= thisConnection
            sendChatMemberCount(connections)
        }
    }
}

private suspend fun sendChatMemberCount(destinations: MutableSet<SocketConnection>) {
    multicast(destinations, ConnectionsDTO(destinations.count()))
}

private suspend inline fun <reified T> multicast(destinations: MutableSet<SocketConnection>, data: T) {
    destinations.forEach {
        it.session.sendSerialized(data)
    }
}