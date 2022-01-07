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
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}
tasks{
    compileKotlin{
        kotlinOptions{
            jvmTarget = "1.8"
        }
    }
    compileTestKotlin{
        kotlinOptions{
            jvmTarget = "1.8"
        }
    }


    test{
        useJUnitPlatform()
    }
}
