plugins {
    java
}

group = "org.meteorlite"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":runelite-annotations"))
    implementation(project(":meteor-logger"))
    implementation(group = "javax.inject", name = "javax.inject", version = "1")
}

tasks {

    compileJava {
        outputs.upToDateWhen { true }
        options.encoding = "UTF-8"

    }
    test {
        useJUnitPlatform()
    }

}