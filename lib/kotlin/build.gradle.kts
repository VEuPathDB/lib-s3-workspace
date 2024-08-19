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

  implementation(libs.kotlin.co.core)
  implementation(libs.kotlin.co.jvm)

  testImplementation(libs.mockito.core)
  testImplementation(libs.junit.core)
  testImplementation(kotlin("test"))
}
