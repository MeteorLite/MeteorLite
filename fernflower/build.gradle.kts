plugins {
    java
}

group "org.meteorlite"
version "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.runelite.net") }
    maven { url = uri("https://raw.githubusercontent.com/open-osrs/hosting/master") }
}

dependencies {
    implementation(project(":openosrs-injector"))
    implementation(project(":meteor-logger"))
}

tasks.test {
    useJUnitPlatform()
}




    //decompileInjected.enabled(false) // enable to have decompiled injected source

    tasks.register<JavaExec>("decompileInjected"){
        if(!state.upToDate){
            classpath(sourceSets["main"].runtimeClasspath)
            main = "org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler"
            args("../openosrs-injector/build/injected/injected-klient.jar", "./build/decompiled/")
            dependsOn(":openosrs-injector:inject")
        }

    }
