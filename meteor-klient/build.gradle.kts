import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.*


plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.compose") version "1.1.0-alpha1-dev536"
    id("io.freefair.lombok") version "6.2.0"
}

group "org.meteorlite"
version "1.0.36"


repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://maven.gegy1000.net/") }
    maven { url = uri("https://repo.runelite.net/") }
    maven { url = uri("https://raw.githubusercontent.com/open-osrs/hosting/master/") }
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev/") }
}

dependencies {

    implementation(group = "com.google.inject", name = "guice", version = "4.1.0", classifier = "no_aop")

    implementation(project(":runescape-api"))
    implementation(project(":runelite-api"))
    implementation(project(":meteor-logger"))
    implementation(project(":http-api"))
    implementation(project(":runelite-annotations"))
    implementation(project(":klient-events"))
    //Deob
    //implementation(project(":runescape-client"))
    //Injected
    implementation(group = "org.jetbrains", name = "annotations", version = "21.0.1")
    runtimeOnly(files("../openosrs-injector/build/injected/injected-klient.jar"))
    //annotationProcessor group =  "org.jetbrains", name =  "annotations", version =  "_"
    implementation(group = "net.runelite.gluegen", name = "gluegen-rt", version = "2.4.0-rc-20200429")
    implementation(group = "net.runelite.jogl", name = "jogl-all", version = "2.4.0-rc-20200429")
    implementation(group = "net.runelite.jocl", name = "jocl", version = "1.0")
    implementation(group = "io.reactivex.rxjava3", name = "rxjava", version = "_")
    implementation(group = "org.apache.commons", name = "commons-lang3", version = "_")
    implementation(group = "io.reactivex.rxjava3", name = "rxjava", version = "3.1.2")
    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = "_")

    implementation(group = "net.runelite.gluegen", name = "gluegen-rt", version = "2.4.0-rc-20200429")
    implementation(group = "net.runelite.jogl", name = "jogl-all", version = "2.4.0-rc-20200429")

    runtimeOnly(
        group = "net.runelite.gluegen",
        name = "gluegen-rt",
        version = "2.4.0-rc-20200429",
        classifier = "natives-linux-amd64"
    )
    runtimeOnly(
        group = "net.runelite.gluegen",
        name = "gluegen-rt",
        version = "2.4.0-rc-20200429",
        classifier = "natives-windows-amd64"
    )
    runtimeOnly(
        group = "net.runelite.gluegen",
        name = "gluegen-rt",
        version = "2.4.0-rc-20200429",
        classifier = "natives-windows-i586"
    )
    runtimeOnly(group = "net.runelite.gluegen", name = "gluegen-rt-natives-macosx", version = "2.4.0-rc-20210117")
    runtimeOnly(
        group = "net.runelite.jogl",
        name = "jogl-all",
        version = "2.4.0-rc-20200429",
        classifier = "natives-linux-amd64"
    )
    runtimeOnly(
        group = "net.runelite.jogl",
        name = "jogl-all",
        version = "2.4.0-rc-20200429",
        classifier = "natives-windows-amd64"
    )
    runtimeOnly(
        group = "net.runelite.jogl",
        name = "jogl-all",
        version = "2.4.0-rc-20200429",
        classifier = "natives-windows-i586"
    )
    runtimeOnly(group = "net.runelite.jogl", name = "jogl-all-natives-macosx", version = "2.4.0-rc-20210117")
    runtimeOnly(group = "net.runelite.jocl", name = "jocl", version = "1.0", classifier = "macos-x64")
    runtimeOnly(group = "net.runelite.jocl", name = "jocl", version = "1.0", classifier = "macos-arm64")

    runtimeOnly(group = "net.runelite.pushingpixels", name = "trident", version = "1.5.00")
    runtimeOnly(group = "net.runelite.jocl", name = "jocl", version = "1.0", classifier = "macos-x64")
    runtimeOnly(group = "net.runelite.jocl", name = "jocl", version = "1.0", classifier = "macos-arm64")
    implementation(group = "com.google.guava", name = "guava", version = "_")
    implementation(group = "org.apache.commons", name = "commons-text", version = "_")
    implementation(group = "commons-io", name = "commons-io", version = "_")
    implementation(group = "net.sf.jopt-simple", name = "jopt-simple", version = "_")
    annotationProcessor(group = "javax.inject", name = "javax.inject", version = "1")
    implementation(group = "io.reactivex.rxjava3", name = "rxjava", version = "_")
    implementation(group = "com.google.code.findbugs", name = "jsr305", version = "_")
    implementation(group = "com.google.code.gson", name = "gson", version = "_")
    implementation(group = "net.lingala.zip4j", name = "zip4j", version = "2.9.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(group = "net.runelite", name = "discord", version = "1.4")
    implementation("org.bouncycastle:bcprov-jdk15on:1.52")
    implementation("org.slf4j:slf4j-api:1.7.12")
    implementation("xerces:xercesImpl:2.12.1")
    implementation("com.formdev:flatlaf:_")
    implementation("com.formdev:flatlaf-intellij-themes:_")
    implementation("com.miglayout:miglayout:3.7.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.5.2")
    implementation("io.insert-koin:koin-core:3.1.4")
    implementation(compose.desktop.currentOs)
    implementation("com.kitfox.svg:svg-salamander:1.0")
    implementation("com.formdev:flatlaf-extras:1.4")
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.20")


}


tasks {
    compose.desktop {

        application {

            mainClass = "Main"
            nativeDistributions {
                targetFormats(Dmg, Msi, Deb)
                includeAllModules = true
            }
            //println(sourceSets["main"].compileClasspath.joinToString("\n"){ it.path} )
            //args("disableGPU")
            jvmArgs(
                "-noverify",
                "-ea",
                "-Xmx2048m",
                "--add-exports", "java.base/java.lang=ALL-UNNAMED",
                "--add-exports", "java.desktop/sun.awt=ALL-UNNAMED",
                "--add-exports", "java.desktop/sun.java2d=ALL-UNNAMED"
            )
        }
    }

    register<Copy>("Copy") {
        from("/")
        into("build/libs/")
        include("run.bat")
    }

    jar {
        manifest {
            attributes(mutableMapOf("Main-class" to "meteor.Main"))
        }
    }
    shadowJar {
        manifest {
            attributes("Main-class" to "Main")
        }
    }

    processResources {
        outputs.upToDateWhen { false }
        dependsOn(project(":openosrs-injector").tasks.getByName("inject"))
        dependsOn(project(":fernflower").tasks.getByName("decompileInjected"))
        dependsOn(project(":klient-mixins").tasks.getByName("compileJava"))
    }
    compileJava {
        options.encoding = "UTF-8"
    }

    compileKotlin {
        kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=all")
    }

    processResources {
        dependsOn(project(":runelite-script-assembler").tasks.getByName("assembleScripts"))
        from("${buildDir}/scripts")
    }

    register<JavaExec>("bootstrap") {
        classpath(sourceSets["main"].runtimeClasspath)
        mainClass.set("meteor.util.bootstrap.Bootstrapper")
    }


}