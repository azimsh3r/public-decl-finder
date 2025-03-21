plugins {
    kotlin("jvm") version "2.1.10"
    application
}

group = "com.github.azimsh3r.breakpointtrackeride"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.1.20")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}