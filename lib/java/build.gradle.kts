plugins {
  kotlin("jvm")
}

kotlin {
  jvmToolchain(16)
}

dependencies {
  api(libs.s34k)
  api(libs.hashID)
  api(libs.slf4j)

  implementation(project(":lib:workspaces-kt"))
  implementation(libs.kotlin.co.core)
  implementation(libs.kotlin.co.jvm)
}