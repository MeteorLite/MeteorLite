plugins {
    java
}

group = "org.meteorlite"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":runelite-api"))
    implementation(project(":meteor-logger"))

    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.20")
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.20")
    implementation(group = "com.google.code.gson", name = "gson", version = "2.8.5")
    implementation(group = "com.google.guava", name = "guava", version = "23.2-jre")
    implementation("com.squareup.okhttp3:okhttp:_")
    implementation(group = "org.apache.commons", name = "commons-csv", version = "1.4")
}

tasks.test {
    useJUnitPlatform()
}