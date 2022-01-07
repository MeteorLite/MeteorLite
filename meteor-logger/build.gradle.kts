plugins {
    id("org.jetbrains.kotlin.jvm")
}

group = "org.meteorlitei"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

}

tasks.test {
    useJUnitPlatform()
}