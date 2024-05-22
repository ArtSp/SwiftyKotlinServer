package com.civitta.data.remote.models

import io.ktor.server.websocket.*
import java.util.concurrent.atomic.*

class Connection(val session: DefaultWebSocketServerSession) {
    companion object {
        val lastId = AtomicInteger(0)
    }
    val sessionID = "${lastId.getAndIncrement()}"
}