rootProject.name = "workspaces"

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      // Versions
      version("s34k", "0.7.2+s34k-0.11.0")
      version("hashID", "1.0.2")
      version("slf4j", "1.7.36")

      version("junit", "5.11.0")
      version("mockito", "5.12.0")

      version("kotlin-coroutines", "1.8.1")

      // Libraries
      library("s34k", "org.veupathdb.lib.s3", "s34k-minio").versionRef("s34k")
      library("hashID", "org.veupathdb.lib", "hash-id").versionRef("hashID")

      library("kotlin.co.core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef("kotlin.coroutines")
      library("kotlin.co.jvm", "org.jetbrains.kotlinx", "kotlinx-coroutines-core-jvm").versionRef("kotlin.coroutines")
      library("slf4j", "org.slf4j", "slf4j-api").versionRef("slf4j")

      library("junit.core", "org.junit.jupiter", "junit-jupiter").versionRef("junit")
      library("mockito.core", "org.mockito", "mockito-core").versionRef("mockito")
    }
  }
}

include(":lib:kotlin")
project(":lib:kotlin").name = "workspaces-kt"

include(":lib:java")
project(":lib:java").name = "workspaces-java"

include(":test")
