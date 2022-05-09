package org.veupathdb.lib.s3.workspaces

import java.io.File
import java.io.InputStream

/**
 * # Workspace File
 *
 * Represents a file in a workspace in the configured S3 store.
 */
interface WorkspaceFile {

  /**
   * Size of this file.
   */
  val size: Long

  /**
   * Base name of this file.
   */
  val name: String

  /**
   * Full path to this file in the S3 bucket.
   */
  val path: String

  /**
   * Opens an input stream over the contents of this file.
   */
  fun open(): InputStream

  /**
   * Copies the contents of the file in the remote store to the given local
   * file.
   *
   * The local file will be truncated before writing.
   *
   * @param localFile Local file into which the remote file's contents will be
   * copied.
   */
  fun copyTo(localFile: File): File

  /**
   * Deletes this file from the remote workspace.
   */
  fun delete()
}