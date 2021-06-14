import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    kotlin("jvm") version "1.5.10"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.5.10"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.5.10"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.5.10"
}

version = "0.0.3"

application {
    mainClass.set("org.radarbase.export.MainKt")
}

project.extra.apply {
    set("okhttpVersion", "4.9.1")
    set("jacksonVersion", "2.12.3")
    set("slf4jVersion", "1.7.30")
    set("logbackVersion", "1.2.3")
    set("openCsvVersion", "4.6")
    set("radarJerseyVersion", "0.6.2")
}

repositories {
    mavenCentral()
}

dependencies {
    api(kotlin("stdlib-jdk8"))

    implementation("com.opencsv:opencsv:${project.extra["openCsvVersion"]}")
    implementation("org.radarbase:radar-jersey:${project.extra["radarJerseyVersion"]}")

    implementation("com.fasterxml.jackson.core:jackson-databind:${project.extra["jacksonVersion"]}")
    implementation("org.slf4j:slf4j-api:${project.extra["slf4jVersion"]}")

    implementation("com.squareup.okhttp3:okhttp:${project.extra["okhttpVersion"]}")

    runtimeOnly("ch.qos.logback:logback-classic:${project.extra["logbackVersion"]}")

    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.register("downloadDependencies") {
    doLast {
        configurations["runtimeClasspath"].files
        configurations["compileClasspath"].files
        println("Downloaded all dependencies")
    }
}


tasks.register<Copy>("copyDependencies") {
    from(configurations.runtimeClasspath.map { it.files })
    into("${buildDir}/third-party")
}

tasks.wrapper {
    gradleVersion = "7.0.2"
}
