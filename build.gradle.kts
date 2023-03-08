import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.8.0"
  id("org.jetbrains.dokka") version "1.7.20"
  java
  `maven-publish`
}

group = "org.veupathdb.lib.s3"
version = "4.1.0"

repositories {
  mavenLocal()
  mavenCentral()

  maven {
    name = "GitHubPackages"
    url  = uri("https://maven.pkg.github.com/veupathdb/packages")
    credentials {
      username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
      password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
    }
  }
}

dependencies {
  implementation(kotlin("stdlib"))

  implementation("org.veupathdb.lib.s3:s34k-minio:0.4.0+s34k-0.8.0")

  implementation("org.veupathdb.lib:hash-id:1.0.2")

  implementation("org.slf4j:slf4j-api:1.7.36")

  implementation("org.apache.logging.log4j:log4j-api:2.17.2")
  implementation("org.apache.logging.log4j:log4j-core:2.17.2")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.2")

  testImplementation("org.mockito:mockito-core:4.5.1")
  testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
  testImplementation(kotlin("test"))
}

java {
  sourceCompatibility = JavaVersion.VERSION_16
  targetCompatibility = JavaVersion.VERSION_16

  withJavadocJar()
  withSourcesJar()
}

tasks.javadoc {
  exclude("module-info.java")
}

tasks.dokkaHtml {
  outputDirectory.set(file("docs/dokka"))
}

tasks.dokkaJavadoc {
  outputDirectory.set(file("docs/javadoc"))
}

task("docs") {
  dependsOn("dokkaHtml", "dokkaJavadoc")
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
