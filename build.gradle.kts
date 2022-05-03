plugins {
    kotlin("jvm") version "1.6.20"
    kotlin("kapt") version "1.6.20"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.github.wow.pvc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-alpha.11")
    implementation("io.requery:requery:1.6.1")
    kapt("io.requery:requery-processor:1.6.1")
    implementation("io.requery:requery-kotlin:1.6.0")
    implementation("org.postgresql:postgresql:42.2.25.jre7")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("ch.qos.logback:logback-core:1.2.11")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        manifest {
            attributes(mapOf("Main-Class" to "com.github.wow.pvc.BootstrapKt"))
        }
    }
}
