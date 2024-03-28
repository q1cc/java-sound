plugins {
    kotlin("jvm") version "1.9.23"
    application
}

group = "net.q1cc.javasound.demo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("net.q1cc.javasound.demo.MainKt")
}
