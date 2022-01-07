plugins {
    java
    id("org.jetbrains.kotlin.jvm")
}

group = "org.meteorlite"
version = "1.0-SNAPSHOT"



repositories {
    mavenCentral()
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(group = "com.google.guava", name = "guava", version = "23.2-jre")
    compileOnly(group = "javax.inject", name = "javax.inject", version = "1")
    compileOnly(group = "commons-io", name = "commons-io", version = "2.8.0")
    compileOnly(project(":runelite-annotations"))
    compileOnly(project(":runescape-api"))
    compileOnly(project(":runescape-client"))
    compileOnly(project(":runelite-api"))
    compileOnly(project(":meteor-logger"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}
tasks{

    java {
        disableAutoTargetJvm()
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    test {
        useJUnitPlatform()
    }
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

}


