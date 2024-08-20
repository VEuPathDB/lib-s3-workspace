import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated

plugins {
  kotlin("jvm") version "2.0.10"
  id("org.jetbrains.dokka") version "1.9.20"
  java
  `maven-publish`
}

group = "org.veupathdb.lib.s3"
version = "5.1.0"

allprojects {
  repositories {
    mavenLocal()
    mavenCentral()

    maven {
      name = "GitHubPackages"
      url = uri("https://maven.pkg.github.com/veupathdb/packages")
      credentials {
        username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
        password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
      }
    }
  }
}

configure(listOf(project(":lib:workspaces-kt"), project(":lib:workspaces-java"))) {
  apply(plugin = "java")
  apply(plugin = "maven-publish")
  apply(plugin = "org.jetbrains.dokka")

  java {
    withJavadocJar()
    withSourcesJar()
  }

  tasks.dokkaHtml {
    outputDirectory.set(file("docs/dokka"))
  }

  tasks.dokkaJavadoc {
    outputDirectory.set(file("docs/javadoc"))
  }

  tasks.test {
    useJUnitPlatform()
  }

  publishing {
    repositories {
      maven {
        name = "GitHub"
        url = uri("https://maven.pkg.github.com/VEuPathDB/lib-s3-workspace")
        credentials {
          username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
          password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
      }
    }

    publications {
      create<MavenPublication>("gpr") {
        artifactId = project.name
        groupId = rootProject.group.toString()
        version = rootProject.version.toString()

        from(components["java"])

        pom {
          name.set("S3 Workspaces")
          description.set("Workspaces backed by an S3 object store.")
          url.set("https://github.com/VEuPathDB/lib-s3-workspace")
          developers {
            developer {
              id.set("epharper")
              name.set("Elizabeth Paige Harper")
              email.set("epharper@upenn.edu")
              url.set("https://github.com/foxcapades")
              organization.set("VEuPathDB")
            }
          }
          scm {
            connection.set("scm:git:git://github.com/VEuPathDB/lib-s3-workspace.git")
            developerConnection.set("scm:git:ssh://github.com/VEuPathDB/lib-s3-workspace.git")
            url.set("https://github.com/VEuPathDB/lib-s3-workspace")
          }
        }
      }
    }
  }
}


task("docs") {
  dependsOn(
    ":lib:workspaces-java:dokkaHtml",
    ":lib:workspaces-java:dokkaJavadoc",
    ":lib:workspaces-kt:dokkaHtml",
    ":lib:workspaces-kt:dokkaJavadoc",
  )

  doLast {
    val docDirs = mapOf(
      file("lib/java/docs/dokka") to file("docs/java/dokka"),
      file("lib/java/docs/javadoc") to file("docs/java/javadoc"),
      file("lib/kotlin/docs/dokka") to file("docs/kotlin/dokka"),
      file("lib/kotlin/docs/javadoc") to file("docs/kotlin/javadoc"),
    )

    for ((from, to) in docDirs) {
      if (!from.exists())
        throw Exception("expected path $from to exist but it didn't")

      if (to.exists())
        to.deleteRecursively()
      else
        to.ensureParentDirsCreated()

      from.copyRecursively(to)
    }
  }
}
