package org.veupathdb.lib.s3.workspaces

import org.veupathdb.lib.s3.s34k.errors.S34KError
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.time.OffsetDateTime

/**
 * # Workspace File
 *
 * Represents a file in a workspace in the configured S3 store.
 *
 * @author Elizabeth Paige Harper [https://github.com/Foxcapades]
 * @since v1.0.0
 */
interface WorkspaceFile {

  /**
   * Size of this file.
   *
   * This field is lazily populated by a call to the S3 store.
   *
   * @throws S34KError If an error is encountered by the underlying library
   * while attempting to communicate with the S3 server.
   */
  @get:Throws(S34KError::class)
  val size: Long

  /**
   * Base name of this file.
   */
  val name: String

  /**
   * Absolute path to this file in the S3 bucket.
   */
  val path: String

  /**
   * Last modified/created timestamp.
   *
   * This property will not be present on file put results.
   */
  val lastModified: OffsetDateTime?

  /**
   * Opens an input stream over the contents of this file.
   *
   * @throws FileNotFoundException If this file no longer exists in the remote
   * workspace.
   *
   * @throws S34KError If an error is encountered by the underlying library
   * while attempting to communicate with the S3 server.
   */
  @Throws(FileNotFoundException::class, S34KError::class)
  fun open(): InputStream

  /**
   * Copies the contents of the file in the remote store to the given local
   * file.
   *
   * The local file will be truncated before writing.
   *
   * @param localFile Local file into which the remote file's contents will be
   * copied.
   *
   * @throws NullPointerException If [localFile] is `null`.
   *
   * @throws FileNotFoundException If this file no longer exists in the remote
   * workspace.
   *
   * @throws S34KError If an error is encountered by the underlying library
   * while attempting to communicate with the S3 server.
   */
  @Throws(NullPointerException::class, FileNotFoundException::class, S34KError::class)
  fun download(localFile: File): File

  /**
   * Deletes this file from the remote workspace.
   *
   * @throws S34KError If an error is encountered by the underlying library
   * while attempting to communicate with the S3 server.
   */
  @Throws(S34KError::class)
  fun delete()
}