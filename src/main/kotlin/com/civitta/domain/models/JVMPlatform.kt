package com.civitta.domain.models

class JVMPlatform {
    val name: String = "Java ${System.getProperty("java.version")}"
    val version: String = "0"
}