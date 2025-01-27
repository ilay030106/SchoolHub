pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google() // Required for Firebase and AndroidX
        mavenCentral() // Required for most dependencies
        jcenter() // Optional, only if you have older dependencies
    }
}

rootProject.name = "SchoolHub"
include(":app")
 