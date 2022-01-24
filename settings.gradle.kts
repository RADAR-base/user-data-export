rootProject.name = "user-data-manager"

pluginManagement {
    val kotlinVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinVersion
    }
}
