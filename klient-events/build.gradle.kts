plugins {
    id( "org.jetbrains.kotlin.jvm")
}

group = "org.meteorlite"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    compileOnly(project(":runelite-api"))
}