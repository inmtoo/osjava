import org.gradle.jvm.tasks.Jar

plugins {
    java
    kotlin("jvm") version "1.3.71"
    kotlin("plugin.serialization") version "1.3.70"
    application
}

sourceSets.main {
    java.srcDir("src/main/kotlin/Main")
}

application {
    mainClassName = "Main"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
//    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.8")
    implementation("org.java-websocket:Java-WebSocket:1.5.1")
    implementation("mysql:mysql-connector-java:8.0.21")
    implementation("org.json:json:20200518")
    implementation(kotlin("stdlib", "1.3.71"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "Main"
    }
}

val fatJar = task("fatJar", type = Jar::class) {
    archiveBaseName.set("webinar")
    manifest {
        attributes["Implementation-Title"] = "Gradle Jar File Example"
        attributes["Implementation-Version"] = archiveVersion
        attributes["Main-Class"] = "Main"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}