plugins{
    java
}

group = "org.meteorlite"
version = "1.0-SNAPSHOT"

repositories{
    mavenCentral()
    maven { url = uri("https://repo.runelite.com")}
    maven { url = uri("https://raw.githubusercontent.com/open-osrs/hosting/master")}
}

dependencies{
    compileOnly("org.projectlombok:lombok:_")
    annotationProcessor("org.projectlombok:lombok:_")
    implementation(gradleApi())

    implementation(project(":runelite-deobfuscator"))
    implementation(project(":runescape-api"))
    implementation(project(":runescape-client"))
    implementation(project(":meteor-logger"))

    implementation(group = "org.ow2.asm", name = "asm", version = "8.0.1")
    implementation(group = "org.ow2.asm", name = "asm-util", version = "8.0.1")
    implementation(group = "org.jetbrains", name = "annotations", version = "19.0.0")
    implementation(group = "com.google.guava", name = "guava", version = "23.2-jre")
    implementation(group = "net.sf.jopt-simple", name = "jopt-simple", version = "5.0.1")

}

tasks{
    test {
        useJUnitPlatform()
    }
    register<JavaExec>("inject"){
        classpath(sourceSets["main"].runtimeClasspath)
        mainClass.set("com.openosrs.injector.Injector")
        dependsOn(":runelite-mixins:jar")
        dependsOn(":klient-mixins:jar")
    }


}


