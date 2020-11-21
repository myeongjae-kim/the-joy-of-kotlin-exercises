import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    id("org.jlleitschuh.gradle.ktlint") version "9.4.0"
}
group = "me.kimmyeongjae"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

object Constants {
    const val JUnitVersion = "5.7.0"
}

dependencies {
    testImplementation(kotlin("test-junit5"))

    testCompileOnly("org.junit.jupiter:junit-jupiter-api:${Constants.JUnitVersion}")
    testCompileOnly("org.junit.jupiter:junit-jupiter-params:${Constants.JUnitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Constants.JUnitVersion}")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

tasks {
    test {
        useJUnitPlatform()
    }
}
