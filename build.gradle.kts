import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    kotlin("jvm") version "1.4.10"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.4.10"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.4.10"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.4.10"
}

application {
    mainClassName = "org.radarbase.export.MainKt"
    version = "0.0.2"
}

project.extra.apply {
    set("okhttpVersion", "4.2.0")
    set("jacksonVersion", "2.9.10")
    set("slf4jVersion", "1.7.27")
    set("logbackVersion", "1.2.3")
    set("grizzlyVersion", "2.4.4")
    set("jerseyVersion", "2.30")
    set("openCsvVersion", "4.6")
    set("radarJerseyVersion", "0.2.2.3")
}

repositories {
    jcenter()
    maven(url = "https://dl.bintray.com/radar-base/org.radarbase")
    maven(url = "https://dl.bintray.com/radar-cns/org.radarcns")
    maven(url = "https://repo.thehyve.nl/content/repositories/snapshots")
}

dependencies {
    api(kotlin("stdlib-jdk8"))

    implementation("com.opencsv:opencsv:${project.extra["openCsvVersion"]}")
    implementation("org.radarbase:radar-jersey:${project.extra["radarJerseyVersion"]}")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${project.extra["jacksonVersion"]}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:${project.extra["jacksonVersion"]}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${project.extra["jacksonVersion"]}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${project.extra["jacksonVersion"]}")
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
    configurations["runtimeClasspath"].files
    configurations["compileClasspath"].files

    doLast {
        println("Downloaded all dependencies")
    }
}


tasks.register<Copy>("copyDependencies") {
    from(configurations.runtimeClasspath.get().files)
    into("${buildDir}/third-party")
}

tasks.wrapper {
    gradleVersion = "6.2.2"
}
