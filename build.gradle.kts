
val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization").version("1.9.22")
    id("io.ktor.plugin") version "2.3.8"
}

group = "com.civitta"
version = "0.0.1"

application {
    mainClass.set("com.civitta.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}

// 1 - Run in terminal to make jar file:  ./gradlew clean jar
// 2 - In build/libs there will appear compiled file <fileName>.jar.
// 3 - Copy via ssh to destination: scp <fileName>.jar <userName>@<IP>:/<path>
// 4 - Run at destination: java -jar <fileName>.jar

tasks.jar {
    // for the case if inside your project there is some duplicate
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest.attributes["Main-Class"] = "com.civitta.ApplicationKt"
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    archiveBaseName.set(project.name + "-all")
}
