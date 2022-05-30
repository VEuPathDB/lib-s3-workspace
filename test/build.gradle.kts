plugins {
  kotlin("jvm") version "1.6.20"
  id("com.github.johnrengelman.shadow") version "7.1.2"
  application
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
  implementation(kotlin("stdlib"))

  implementation("org.veupathdb.lib:hash-id:2.0.0")
  implementation("org.veupathdb.lib.s3:s34k:0.4.0")
  implementation("org.veupathdb.lib.s3:workspaces:2.2.0")

  implementation("org.slf4j:slf4j-api:1.7.36")

  implementation("org.apache.logging.log4j:log4j-api:2.17.2")
  implementation("org.apache.logging.log4j:log4j-core:2.17.2")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.2")
}

application {
  mainClass.set("test.AppKt")
}

tasks.shadowJar {
  archiveBaseName.set("service")
  archiveClassifier.set("")
  archiveVersion.set("")

  exclude("**/Log4j2Plugins.dat")
}

java {
  sourceCompatibility = JavaVersion.VERSION_16
  targetCompatibility = JavaVersion.VERSION_16
}

tasks.test {
  useJUnitPlatform()
}