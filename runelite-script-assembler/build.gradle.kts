plugins{
    java
}

group = "org.meteorlite"
version = "1.0-SNAPSHOT"

repositories{
    maven {url =  uri("https://repo.runelite.net/")}
    mavenCentral()
}
dependencies {
    annotationProcessor(group = "org.eclipse.sisu", name = "org.eclipse.sisu.inject", version = "0.3.4")

    compileOnly(group = "org.apache.maven.plugin-tools", name = "maven-plugin-annotations", version = "3.6.0")
    implementation("net.runelite:cache:1.8.4")
    implementation(project(":runelite-api"))
    implementation(project(":meteor-logger"))
    implementation(group = "com.google.guava", name = "guava", version = "23.2-jre")
    implementation(group = "org.apache.maven", name = "maven-plugin-api", version = "3.6.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:_")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:_")
}

tasks{
    test{
        useJUnitPlatform()
    }
    register<JavaExec>("assembleScripts"){
        outputs.cacheIf{true}
        classpath(sourceSets["main"].runtimeClasspath)
        mainClass.set("net.runelite.script.AssembleMojo")
    }
}