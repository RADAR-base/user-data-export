import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
}

version = "0.0.3"

application {
    mainClass.set("org.radarbase.export.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    api(kotlin("stdlib-jdk8"))

    val openCsvVersion: String by project
    implementation("com.opencsv:opencsv:$openCsvVersion")

    val radarJerseyVersion: String by project
    implementation("org.radarbase:radar-jersey:$radarJerseyVersion")

    val jacksonVersion: String by project
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

    val slf4jVersion: String by project
    implementation("org.slf4j:slf4j-api:$slf4jVersion")

    val okhttpVersion: String by project
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")

    val logbackVersion: String by project
    runtimeOnly("ch.qos.logback:logback-classic:$logbackVersion")

    val junitVersion: String by project
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

val downloadDependencies by tasks.registering {
    doLast {
        configurations.runtimeClasspath.map { it.files }
        configurations.compileClasspath.map { it.files }
        println("Downloaded all dependencies")
    }
}

val copyDependencies by tasks.registering(Copy::class) {
    from(configurations.runtimeClasspath.map { it.files })
    into("$buildDir/third-party")
}

tasks.register("prepareDockerEnvironment") {
    val startScripts by tasks.getting
    dependsOn(downloadDependencies, copyDependencies, startScripts)
}

tasks.wrapper {
    gradleVersion = "7.3.3"
}
