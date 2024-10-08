package test

import org.slf4j.LoggerFactory
import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.s3.s34k.S3Client
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.lib.s3.workspaces.java.WorkspaceAlreadyExistsError
import org.veupathdb.lib.s3.workspaces.java.S3WorkspaceFactory

// Workspace factory based in the bucket root.
class RootWorkspaceFactory(private val s3: S3Client) {

  private val log = LoggerFactory.getLogger("Root WorkspaceFactory")

  fun run(): TestResult {
    val out = TestResult()

    out += getWhenWorkspaceNotExists()
    out += getWhenWorkspaceExists()

    out += createWhenWorkspaceNotExists()
    out += createWhenWorkspaceExists()

    return out
  }

  private fun getWhenWorkspaceNotExists(): Boolean {
    log.info("WorkspaceFactory.get when workspace does not exist.")

    // Setup
    val bucket = s3.buckets.create(BucketName("foobar"))
    val hashID = HashID("3bbfaace3b2fc985b4ee7bf0524fe938")

    try {
      val out = S3WorkspaceFactory(s3, bucket.name.name).get(hashID) == null

      if (!out)
        log.error("Failed!")

      return out
    } finally {
      bucket.delete()
    }
  }

  private fun getWhenWorkspaceExists(): Boolean {
    log.info("WorkspaceFactory.get when workspace does exist.")

    val bucket = s3.buckets.create(BucketName("foobar"))
    val hashID = HashID("d2ef1720c8ca990a8fed7847c3e26684")

    bucket.objects.touch("d2ef1720c8ca990a8fed7847c3e26684/.workspace")

    try {
      val out = S3WorkspaceFactory(s3, bucket.name.name).get(hashID) != null

      if (!out)
        log.error("Failed!")

      return out
    } finally {
      bucket.deleteRecursive()
    }
  }

  private fun createWhenWorkspaceNotExists(): Boolean {
    log.info("WorkspaceFactory.create when workspace does not exist.")

    val bucket = s3.buckets.create(BucketName("foobar"))
    val hashID = HashID("90a4e75b78b3137c2d1d175e15dcb7fb")

    try {
      S3WorkspaceFactory(s3, bucket.name.name).create(hashID)

      if ("90a4e75b78b3137c2d1d175e15dcb7fb/.workspace" !in bucket.objects) {
        log.error("Failed!")
        return false
      }

      return true
    } finally {
      bucket.deleteRecursive()
    }
  }

  private fun createWhenWorkspaceExists(): Boolean {
    log.info("WorkspaceFactory.create when workspace already exists.")

    val bucket = s3.buckets.create(BucketName("foobar"))
    val hashID = HashID("5ead3556b4924a5bc3fb1bd262575dae")

    bucket.objects.touch("5ead3556b4924a5bc3fb1bd262575dae/.workspace")

    return try {
      S3WorkspaceFactory(s3, bucket.name.name).create(hashID)
      false
    } catch (e: WorkspaceAlreadyExistsError) {
      true
    } finally {
      bucket.deleteRecursive()
    }
  }
}