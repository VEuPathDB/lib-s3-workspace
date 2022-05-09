plugins {
  kotlin("jvm") version "1.6.20"
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
  implementation(kotlin("stdlib-jdk8"))

  implementation("org.veupathdb.lib.s3:s34k:0.4.0")
  implementation("org.veupathdb.lib.s3:s34k-minio:0.2.0+s34k-0.4.0")

  implementation("org.veupathdb.lib:hash-id:2.0.0")

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
}

tasks.test {
  useJUnitPlatform()
}