package test

import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.S3Config

  private val log = LoggerFactory.getLogger("Tester")

fun main() {
  val client = S3Api.newClient(S3Config("minio", 80u, false, System.getenv("ACCESS_KEY"), System.getenv("ACCESS_TOKEN")))
  val result = TestResult()

  result += RootWorkspaceFactory(client).run()
  result += NestedWorkspaceFactory(client).run()

  log.info("Succeeded: {}", result.successes)
  log.info("   Failed: {}", result.failures)
}