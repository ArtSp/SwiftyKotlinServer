package com.civitta.data.remote.plugins

import io.ktor.server.application.*
import com.civitta.data.remote.Constants
import io.ktor.server.websocket.*
import com.civitta.data.remote.models.ServerDateDTO
import com.civitta.data.remote.models.chat.ConnectionsDTO
import com.civitta.data.remote.models.chat.MessageDTO
import com.civitta.data.remote.models.Connection
import io.ktor.server.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.datetime.Clock
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
}

// Websocket 1:many
// Multicast server message to all listeners "bye" will terminate socket.
private fun Routing.chatSocket() {
    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
    
    webSocket(Constants.Path.WS_SERVER_CHAT) {
        val thisConnection = Connection(this)
        connections += thisConnection
        
        val name = call.request.queryParameters["name"] ?: "Unnamed ${thisConnection.sessionID}"
        println("User $name joined chat")
        
        try {
            sendChatMemberCount(connections)
            
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val message = MessageDTO(
                    sender = name,
                    date = Clock.System.now(),
                    message = frame.readText()
                )
                sendMessage(connections, message)
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

private suspend fun sendChatMemberCount(destinations: MutableSet<Connection>) {
    val data = ConnectionsDTO(destinations.count())
    destinations.forEach {
        it.session.sendSerialized(data)
    }
}

private suspend fun sendMessage(destinations: MutableSet<Connection>, messageDTO: MessageDTO) {
    destinations.forEach {
        it.session.sendSerialized(messageDTO)
    }
}