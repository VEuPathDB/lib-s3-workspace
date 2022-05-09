package test

import org.slf4j.LoggerFactory
import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.s3.s34k.S3Client
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.lib.s3.workspaces.WorkspaceFactory

// Workspace factory based in the bucket root.
class RootWorkspaceFactory(private val s3: S3Client) {

  private val log = LoggerFactory.getLogger("Root WorkspaceFactory")

  fun run(): TestResult {


    val out = TestResult()

    out += getWhenWorkspaceNotExists()
    out += getWhenWorkspaceExists()

    return out
  }

  private fun getWhenWorkspaceNotExists(): Boolean {
    log.info("WorkspaceFactory.get when workspace does not exist.")

    // Setup
    val bucket = s3.buckets.create(BucketName("foobar"))
    val hashID = HashID.ofHash("3bbfaace3b2fc985b4ee7bf0524fe938")

    try {
      val out = WorkspaceFactory(s3, bucket.name.name)[hashID] == null

      if (!out)
        log.error("Failed!")

      return out
    } finally {
      bucket.delete()
    }
  }

  private fun getWhenWorkspaceExists(): Boolean {
    log.info("WorkspaceFactory.get when workspace does exist.")

    val bucket = s3.buckets.create(BucketName("fizzbuzz"))
    val hashID = HashID.ofHash("d2ef1720c8ca990a8fed7847c3e26684")

    bucket.objects.touch("d2ef1720c8ca990a8fed7847c3e26684/.workspace")

    try {
      val out = WorkspaceFactory(s3, bucket.name.name)[hashID] != null

      if (!out)
        log.error("Failed!")

      return out
    } finally {
      bucket.deleteRecursive()
    }

  }
}