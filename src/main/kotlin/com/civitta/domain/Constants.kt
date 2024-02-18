package com.civitta.domain

sealed class Constants {
    companion object {
        const val SERVER_PORT = 8080
    }

    sealed class Path {
        companion object {
            const val GET_VERSION = "/serverVersion"
        }
    }
}