plugins{
    java
}
group = "org.meteorlite"
version = "1.0-SNAPSHOT"

repositories{
    mavenCentral()
}
dependencies{
    implementation(project(":runelite-annotations"))
    implementation(project(":meteor-logger"))
    implementation("org.bouncycastle:bcprov-jdk15on:1.52")
    compileOnly("org.projectlombok:lombok:1.18.20")

    testImplementation("org.junit.jupiter:junit-jupiter-api:_")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-api:_")
    implementation("com.google.code.gson:gson:_")
}

tasks{
    withType<JavaCompile>(){
        options.encoding = "UTF-8"
    }
    java{

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        disableAutoTargetJvm()
    }
    withType<JavaExec>(){
       classpath = sourceSets["main"].runtimeClasspath
        mainClass.set("net.runelite.rs.UpdateRefMap")
    }
    test{
        useJUnitPlatform()
    }
}
