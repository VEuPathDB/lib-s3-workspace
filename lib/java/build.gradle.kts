plugins {
  kotlin("jvm")
}

kotlin {
  jvmToolchain(16)
}

dependencies {
  implementation(project(":lib:workspaces-kt"))
  implementation(libs.kotlin.co.core)
  implementation(libs.kotlin.co.jvm)
}