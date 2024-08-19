package org.veupathdb.lib.s3.workspaces.java

import kotlinx.coroutines.runBlocking

import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.s3.s34k.S3Client
import org.veupathdb.lib.s3.s34k.errors.S34KError
import org.veupathdb.lib.s3.s34k.fields.BucketName

import org.veupathdb.lib.s3.workspaces.S3WorkspaceFactory as KTFactory
import org.veupathdb.lib.s3.workspaces.WorkspaceAlreadyExistsError as KTError

/**
 * # S3 Workspace Factory
 *
 * Creates workspace instances backed by S3 files/directories.
 *
 * @author Elizabeth Paige Harper [https://github.com/Foxcapades]
 */
@Suppress("unused")
class S3WorkspaceFactory(private val delegate: KTFactory) {

  /**
   * Constructs a new [S3WorkspaceFactory] instance with an empty default root
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
  constructor(s3: S3Client, bucket: String) : this(KTFactory(s3, bucket))

  /**
   * Constructs a new [S3WorkspaceFactory] instance with an empty default root
   * directory.
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
  constructor(s3: S3Client, bucket: String, rootDir: String) : this(KTFactory(s3, bucket, rootDir))

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
  fun get(id: HashID) = runBlocking { delegate.get(id) }

  /**
   * Creates a new workspace.
   *
   * If `rootDir` was set when constructing this [S3WorkspaceFactory] instance,
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
   * @return A [S3Workspace] instance wrapping the created workspace.
   *
   * @throws NullPointerException If [id] is `null`.
   *
   * @throws WorkspaceAlreadyExistsError If the target workspace already exists.
   *
   * @throws S34KError If an error is encountered by the underlying library
   * while attempting to communicate with the S3 server.
   */
  @Throws(NullPointerException::class, WorkspaceAlreadyExistsError::class, S34KError::class)
  fun create(id: HashID) = runBlocking {
    try {
      delegate.create(id)
    } catch (e: KTError) {
      throw WorkspaceAlreadyExistsError(e.id)
    }
  }
}