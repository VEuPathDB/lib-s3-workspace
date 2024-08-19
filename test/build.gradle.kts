plugins {
  kotlin("jvm")
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

kotlin {
  jvmToolchain(16)
}

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
  implementation(project(":lib:workspaces-java"))

  implementation("org.apache.logging.log4j:log4j-api:2.17.2")
  implementation("org.apache.logging.log4j:log4j-core:2.17.2")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.2")
}

tasks.shadowJar {
  archiveBaseName.set("service")
  archiveClassifier.set("")
  archiveVersion.set("")

  manifest {
    attributes["Main-Class"] = "test.AppKt"
  }

  exclude("**/Log4j2Plugins.dat")
}

tasks.test {
  useJUnitPlatform()
}