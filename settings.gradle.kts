rootProject.name = "Prisoners-Dilemma"

pluginManagement {
    repositories {
        maven(url = "https://maven.pkg.jetbrains.space/public/p/krpc/maven") // todo update repo link
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
