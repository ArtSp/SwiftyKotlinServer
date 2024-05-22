package com.civitta.data.remote

sealed class Constants {
    companion object {
        const val SERVER_PORT = 8080
    }

    sealed class Path {
        companion object {
            const val GET_VERSION = "/serverVersion"
            const val WS_SERVER_TIME = "/serverTime"
            const val WS_SERVER_CHAT = "/chat" //expected query "name": String
        }
    }
}