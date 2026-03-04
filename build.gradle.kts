plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    kotlin("plugin.serialization") version "1.9.24" // O la versión de tu Kotlin
}

group = "com.MindStack"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    val ktor_version = "3.0.3"

    // Core y Engine
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)

    // --- SERIALIZACIÓN ---
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version") // <-- ESTA ES LA QUE TE FALTA

    // --- MANEJO DE ERRORES ---
    implementation("io.ktor:ktor-server-status-pages:$ktor_version") // <-- ESTA ES LA QUE TE FALTA

    // --- SEGURIDAD (CORS y AUTH) ---
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("com.auth0:java-jwt:4.4.0")

    // --- BASE DE DATOS ---
    implementation("org.postgresql:postgresql:42.7.7")
    val exposed_version = "0.44.1"
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.mindrot:jbcrypt:0.4")

    // Mantén Hikari y Postgres como estaban
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Testing
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
