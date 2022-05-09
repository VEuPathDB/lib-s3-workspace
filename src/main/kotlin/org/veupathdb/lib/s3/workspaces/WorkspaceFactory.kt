package org.veupathdb.lib.s3.workspaces

import org.slf4j.LoggerFactory
import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.s3.s34k.S3Client
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.errors.S34KError
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.lib.s3.workspaces.impl.MarkerFile
import org.veupathdb.lib.s3.workspaces.impl.WorkspaceImpl
import org.veupathdb.lib.s3.workspaces.util.extendPath
import org.veupathdb.lib.s3.workspaces.util.validatePath

/**
 * # S3 Workspace Factory
 *
 * Creates workspace instances backed by S3 files/directories.
 *
 * @author Elizabeth Paige Harper [https://github.com/Foxcapades]
 * @since v1.0.0
 *
 * @constructor Constructs a new [WorkspaceFactory] instance.
 *
 * @param s3 Initialized [S3Client] instance that will be used for all 'file
 * operations'.
 *
 * @param bucket Name of the bucket that this factory and the workspaces it
 * opens/creates reside in.  If this bucket does not exist, an
 * [IllegalStateException] will be thrown on initialization of this type.
 *
 * @param rootDir Root directory that all workspaces reside in.  All operations
 * on this type will be relative to this root directory.  If the workspace root
 * is the root of the bucket, set this value to an empty string.
 *
 * @throws IllegalStateException If the target bucket for this workspace root
 * does not exist.
 *
 * @throws IllegalArgumentException If the given bucket name is not a valid S3
 * bucket name.  For additional rules on what a valid bucket name looks like,
 * see [BucketName].
 *
 * @throws IllegalArgumentException If the given [rootDir] value is not a valid
 * path segment.
 *
 * @throws S34KError If an error is encountered by the underlying library
 * while attempting to communicate with the S3 server.
 */
@Suppress("unused")
class WorkspaceFactory(
  private val s3: S3Client,
  bucket: String,
  private val rootDir: String,
) {

  private val log = LoggerFactory.getLogger(WorkspaceFactory::class.java)

  private val bucket: S3Bucket

  /**
   * Constructs a new [WorkspaceFactory] instance with an empty default root
   * directory.
   *
   * @param s3 Initialized [S3Client] instance that will be used for all 'file
   * operations'.
   *
   * @param bucket Name of the bucket that this factory and the workspaces it
   * opens/creates reside in.  If this bucket does not exist, an
   * [IllegalStateException] will be thrown on initialization of this type.
   *
   * @throws IllegalStateException If the target bucket for this workspace root
   * does not exist.
   *
   * @throws IllegalArgumentException If the given bucket name is not a valid S3
   * bucket name.  For additional rules on what a valid bucket name looks like,
   * see [BucketName].
   *
   * @throws S34KError If an error is encountered by the underlying library
   * while attempting to communicate with the S3 server.
   */
  constructor(s3: S3Client, bucket: String) : this(s3, bucket, "")

  init {
    rootDir.validatePath()

    this.bucket = s3.buckets[BucketName(bucket)] ?: throw IllegalStateException("Bucket '$bucket' does not exist.")
  }

  /**
   * Looks up and returns the workspace for the given [HashID].
   *
   * If no such workspace directory exists, this method returns `null`.
   *
   * @param id ID of the workspace to return.
   *
   * @return A workspace instance, if the target workspace exists, otherwise
   * `null`.
   *
   * @throws NullPointerException If [id] is `null`.
   *
   * @throws S34KError If an error is encountered by the underlying library
   * while attempting to communicate with the S3 server.
   */
  @Throws(NullPointerException::class, S34KError::class)
  operator fun get(id: HashID): Workspace? {
    log.trace("#get(id = {})", id)

    val path = makePath(id)
    val mark = path.extendPath(MarkerFile)

    log.debug("Test path = $mark")

    // Test that the marker object exists.  If it does not, return null.
    return if (mark !in bucket.objects)
      null
    else
      WorkspaceImpl(id, bucket, path)
  }

  /**
   * Creates a new workspace.
   *
   * If [rootDir] was set when constructing this [WorkspaceFactory] instance,
   * the created workspace will reside under that root directory.
   *
   * If a workspace already exists with the given ID, this method will fail with
   * a [WorkspaceAlreadyExistsError].
   *
   * As S3 has no concept of directories, a workspace is marked using an empty
   * file named '.workspace' in the workspace root.  This file will not be
   * listed when attempting to fetch a list of files in the workspace.
   *
   * @param id ID of the workspace to create.
   *
   * @return A [Workspace] instance wrapping the created workspace.
   *
   * @throws NullPointerException If [id] is `null`.
   *
   * @throws WorkspaceAlreadyExistsError If the target workspace already exists.
   *
   * @throws S34KError If an error is encountered by the underlying library
   * while attempting to communicate with the S3 server.
   */
  @Throws(NullPointerException::class, WorkspaceAlreadyExistsError::class, S34KError::class)
  fun create(id: HashID): Workspace {
    log.trace("#create(id = {})", id)

    val wsPath = makePath(id)
    val marker = wsPath.extendPath(MarkerFile)

    log.debug("Testing if workspace $id exists")
    if (marker in bucket.objects)
      throw WorkspaceAlreadyExistsError(id)

    log.debug("Creating workspace marker file $marker")
    bucket.objects.touch(marker)

    return WorkspaceImpl(id, bucket, wsPath)
  }

  @Suppress("NOTHING_TO_INLINE")
  private inline fun makePath(id: HashID) = rootDir.extendPath(id.string)
}