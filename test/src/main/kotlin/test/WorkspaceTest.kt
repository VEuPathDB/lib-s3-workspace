package test

import org.slf4j.LoggerFactory
import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.s3.s34k.S3Client
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.lib.s3.workspaces.java.S3WorkspaceFactory

class WorkspaceTest(private val s3: S3Client) {

  private val log = LoggerFactory.getLogger("Workspace")

  fun run(): TestResult {
    val result = TestResult()

    result += existsWhenExists()
    result += existsWhenNotExists()
    result += touch()
    result += write()
    result += open()
    result += list()
    result += containsWhenExists()
    result += containsWhenNotExists()

    return result
  }

  private fun existsWhenExists(): Boolean {
    log.info("Workspace.exists when the workspace exists.")

    val bucket  = s3.buckets.create(BucketName("foobar"))
    val hashID  = HashID.ofMD5("flowers")
    val factory = S3WorkspaceFactory(s3, "foobar")

    try {
      // Create workspace
      bucket.objects.touch("${hashID.string}/.workspace")

      val ws = factory.get(hashID)

      if (ws == null) {
        log.error("Failed! (ws was null)")
        return false
      }

      if (!ws.exists()) {
        log.error("Failed! (ws did not exist)")
        return false
      }

      return true
    } finally {
      bucket.deleteRecursive()
    }
  }

  private fun existsWhenNotExists(): Boolean {
    log.info("Workspace.exists when the workspace does not exist")

    val bucket  = s3.buckets.create(BucketName("foobar"))
    val hashID  = HashID.ofMD5("bonkers")
    val factory = S3WorkspaceFactory(s3, "foobar")

    try {
      // Create workspace marker so we can get a handle on the workspace
      val touched = bucket.objects.touch("${hashID.string}/.workspace")

      val ws = factory.get(hashID)

      if (ws == null) {
        log.error("Failed! (ws was null)")
        return false
      }

      // delete the workspace marker now so the workspace no longer exists.
      touched.delete()

      if (ws.exists()) {
        log.error("Failed! (ws still exists)")
        return false
      }

      return true
    } finally {
      bucket.deleteRecursive()
    }
  }

  private fun touch(): Boolean {
    log.info("Workspace.touch")

    val bucket  = s3.buckets.create(BucketName("foobar"))
    val hashID  = HashID.ofMD5("flumps")
    val factory = S3WorkspaceFactory(s3, "foobar")

    try {
      // Create workspace marker so we can get a handle on the workspace
      bucket.objects.touch("${hashID.string}/.workspace")

      val ws = factory.get(hashID)

      if (ws == null) {
        log.error("Failed! (ws was null)")
        return false
      }

      ws.touch("yapyapyap")

      if (!bucket.objects.contains("${hashID.string}/yapyapyap")) {
        log.error("Failed! (touched file does not exist)")
        return false
      }

      return true
    } finally {
      bucket.deleteRecursive()
    }
  }

  private fun write(): Boolean {
    log.info("Workspace.write")

    val bucket  = s3.buckets.create(BucketName("foobar"))
    val hashID  = HashID.ofMD5("postgres")
    val factory = S3WorkspaceFactory(s3, "foobar")

    val input = "hello world!"

    try {
      // Create workspace marker so we can get a handle on the workspace
      bucket.objects.touch("${hashID.string}/.workspace")

      val ws = factory.get(hashID)

      if (ws == null) {
        log.error("Failed! (ws was null)")
        return false
      }

      ws.write("doorknob", input.byteInputStream())

      val obj = bucket.objects.open("${hashID.string}/doorknob")

      if (obj == null) {
        log.error("Failed! (remote object was not created)")
        return false
      }

      val contents = obj.stream.readAllBytes().decodeToString()
      obj.stream.close()

      if (contents != input) {
        log.error("Failed! (remote object contents differed from input)")
        return false
      }

      return true
    } finally {
      bucket.deleteRecursive()
    }
  }

  private fun open(): Boolean {
    log.info("Workspace.open")

    val bucket  = s3.buckets.create(BucketName("foobar"))
    val hashID  = HashID.ofMD5("postgres")
    val factory = S3WorkspaceFactory(s3, "foobar")

    val input = "goodbye world!"

    try {
      // Create workspace marker so we can get a handle on the workspace
      bucket.objects.touch("${hashID.string}/.workspace")

      val ws = factory.get(hashID)

      if (ws == null) {
        log.error("Failed! (ws was null)")
        return false
      }

      // put the object we're going to open into the store
      bucket.objects["flamingo"] = input.byteInputStream()

      // Read the contents back out of the store
      val contents = ws.open("flamingo").use { it.readAllBytes().decodeToString() }

      if (contents != input) {
        log.error("Failed! (remote object contents differed from input)")
        return false
      }

      return true
    } finally {
      bucket.deleteRecursive()
    }
  }

  private fun list(): Boolean {
    log.info("Workspace.list")

    val bucket  = s3.buckets.create(BucketName("foobar"))
    val hashID  = HashID.ofMD5("postgres")
    val factory = S3WorkspaceFactory(s3, "foobar")

    try {
      // Create workspace marker so we can get a handle on the workspace
      bucket.objects.touch("${hashID.string}/.workspace")

      // Create the objects we're going to list
      bucket.objects.touch("${hashID.string}/bananas")
      bucket.objects.touch("${hashID.string}/speaker")
      bucket.objects.touch("${hashID.string}/oranges")

      val ws = factory.get(hashID)

      if (ws == null) {
        log.error("Failed! (ws was null)")
        return false
      }

      val list = ws.files()

      if (list.size != 3) {
        log.error("Failed! (remote object list contained the wrong number of entries)")
        return false
      }

      if (list[0].relativePath != "bananas") {
        log.error("Failed! (remote object list contains invalid entries)")
        return false
      }
      if (list[1].relativePath != "oranges") {
        log.error("Failed! (remote object list contains invalid entries)")
        return false
      }
      if (list[2].relativePath != "speaker") {
        log.error("Failed! (remote object list contains invalid entries)")
        return false
      }

      return true
    } finally {
      bucket.deleteRecursive()
    }
  }

  private fun containsWhenExists(): Boolean {
    log.info("Workspace.contains when target file exists")

    val bucket  = s3.buckets.create(BucketName("foobar"))
    val hashID  = HashID.ofMD5("postgres")
    val factory = S3WorkspaceFactory(s3, "foobar")
    val objName = "starlight-brigade"

    try {
      // Create workspace marker so we can get a handle on the workspace
      bucket.objects.touch("${hashID.string}/.workspace")

      // Create the object we're going to test for
      bucket.objects.touch("${hashID.string}/$objName")

      val ws = factory.get(hashID)

      if (ws == null) {
        log.error("Failed! (ws was null)")
        return false
      }

      if (!ws.contains(objName)) {
        log.error("Failed! (contains returned false)")
        return false
      }

      return true
    } finally {
      bucket.deleteRecursive()
    }
  }

  private fun containsWhenNotExists(): Boolean {
    log.info("Workspace.contains when target file does not exist")

    val bucket  = s3.buckets.create(BucketName("foobar"))
    val hashID  = HashID.ofMD5("postgres")
    val factory = S3WorkspaceFactory(s3, "foobar")
    val objName = "the-hit"

    try {
      // Create workspace marker so we can get a handle on the workspace
      bucket.objects.touch("${hashID.string}/.workspace")

      val ws = factory.get(hashID)

      if (ws == null) {
        log.error("Failed! (ws was null)")
        return false
      }

      if (ws.contains(objName)) {
        log.error("Failed! (contains returned true)")
        return false
      }

      return true
    } finally {
      bucket.deleteRecursive()
    }
  }
}