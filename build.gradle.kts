plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("kapt") version "1.5.31"
}

group = "com.github.wow.pvc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    implementation("net.dv8tion:JDA:4.3.0_333")
    implementation("io.requery:requery:1.6.1")
    kapt("io.requery:requery-processor:1.6.1")
    implementation("io.requery:requery-kotlin:1.6.0")
    implementation("org.postgresql:postgresql:42.2.24.jre7")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("ch.qos.logback:logback-classic:1.2.6")
    implementation("ch.qos.logback:logback-core:1.2.6")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
