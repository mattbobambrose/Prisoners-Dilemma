val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val coroutinesVersion: String by project
val iterVersion: String by project
val loggingVersion: String by project
val logbackVersion: String by project

plugins {
    val versionsVersion: String by System.getProperties()
    kotlin("jvm") version "1.9.24"
    id("io.ktor.plugin") version "2.3.11"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24"
//    kotlin("plugin.power-assert") version "1.9.24"
    id("com.google.devtools.ksp") version "1.9.24-1.0.20"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.1.0"
    id("com.github.ben-manes.versions") version versionsVersion
}
group = "org.example"

version = "1.0-SNAPSHOT"

repositories {
    maven("https://maven.pkg.jetbrains.space/public/p/krpc/maven")
    mavenCentral()
}

application {
    mainClass.set("com.example.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation("io.github.oshai:kotlin-logging-jvm:$loggingVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("com.michael-bull.kotlin-itertools:kotlin-itertools:$iterVersion")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-resources")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-client-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-html-builder-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")
    implementation("io.ktor:ktor-server-websockets-jvm")
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging:$ktor_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")

    implementation("org.jetbrains.kotlinx:kotlinx-rpc-runtime-client:1.9.24-0.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-rpc-runtime-server:1.9.24-0.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-runtime-serialization-json")

    implementation("org.jetbrains.kotlinx:kotlinx-rpc-transport-ktor-client")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-transport-ktor-server")

    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")

    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

//tasks.test {
//    useJUnitPlatform()
//}
kotlin {
    jvmToolchain(17)
}