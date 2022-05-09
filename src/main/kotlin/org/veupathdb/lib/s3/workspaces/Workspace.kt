package org.veupathdb.lib.s3.workspaces

import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.s3.s34k.errors.S34KError
import org.veupathdb.lib.s3.s34k.errors.ObjectNotFoundError
import java.io.File
import java.io.InputStream
import java.io.FileNotFoundException

/**
 * # Workspace
 *
 * Represents a workspace for a job in which workspace specific files and
 * directories reside.
 *
 * A workspace may have sub-workspaces contained inside it which are accessible
 * via the [openSubWorkspace] method.
 *
 * @author Elizabeth Paige Harper [https://github.com/Foxcapades]
 * @since v1.0.0
 */
interface Workspace {

  /**
   * ID of this workspace.
   */
  val id: HashID

  /**
   * Tests whether this workspace still exists.
   *
   * @return `true` if this workspace still exists, otherwise `false`.
   *
   * @throws S34KError If an error occurs in the underlying library while
   * communicating with the S3 server.
   */
  @Throws(S34KError::class)
  fun exists(): Boolean

  /**
   * Creates an empty file in this workspace with the given [path] if no such
   * file already exists.
   *
   * @param path Relative path to the empty file to create.
   *
   * @return A [WorkspaceFile] wrapping the target file (the empty file if the
   * file did not already exist, or the existing file if it did already exist).
   *
   * @throws NullPointerException If the given [path] value is `null`.
   *
   * @throws IllegalArgumentException If the given [path] value is blank.
   *
   * @throws S34KError If an error occurs in the underlying library while
   * communicating with the S3 server.
   */
  @Throws(NullPointerException::class, IllegalArgumentException::class, S34KError::class)
  fun touch(path: String) : WorkspaceFile

  /**
   * Writes the given [InputStream] to a file at the given [path].
   *
   * If a file already exists with the target [path], it will be overwritten.
   *
   * @param path Relative path to the empty file to create.
   *
   * @param stream InputStream over the contents to be written to the remote
   * file.
   *
   * @return A [WorkspaceFile] instance wrapping the created file.
   *
   * @throws NullPointerException If either [path] or [stream] is `null`.
   *
   * @throws IllegalArgumentException If the given [path] value is blank.
   *
   * @throws S34KError If an error occurs in the underlying library while
   * communicating with the S3 server.
   */
  @Throws(NullPointerException::class, IllegalArgumentException::class, S34KError::class)
  fun write(path: String, stream: InputStream) : WorkspaceFile

  /**
   * Uploads the given [File] to a new file in the remote workspace under the
   * given [path].
   *
   * If a file already exists with the target [path], it will be overwritten.
   *
   * @param path Relative path to the empty file to create.
   *
   * @param localFile File that will be uploaded to the remote store.
   *
   * @return A [WorkspaceFile] wrapping the uploaded file.
   *
   * @throws NullPointerException If either [path] or [localFile] is null.
   *
   * @throws IllegalArgumentException If the given [path] is blank.
   *
   * @throws FileNotFoundException If the given [localFile] does not exist.
   *
   * @throws S34KError If an error occurs in the underlying library while
   * communicating with the S3 server.
   */
  @Throws(
    NullPointerException::class,
    IllegalArgumentException::class,
    FileNotFoundException::class,
    S34KError::class
  )
  fun write(path: String, localFile: File) : WorkspaceFile

  /**
   * Opens a stream over the contents of the target file in the S3 store.
   *
   * @param path Relative path to the remote file to open.
   *
   * @return An [InputStream] over the contents of the remote file.
   *
   * @throws NullPointerException If [path] is `null`.
   *
   * @throws IllegalArgumentException If [path] is blank.
   *
   * @throws FileNotFoundException If [path] was not found in this workspace.
   *
   * @throws S34KError If an error occurs in the underlying library while
   * communicating with the S3 server.
   */
  @Throws(
    NullPointerException::class,
    IllegalArgumentException::class,
    FileNotFoundException::class,
    S34KError::class
  )
  fun open(path: String): InputStream

  /**
   * Copies a file from the remote store to the specified local [target] file.
   *
   * @param source Relative path to the remote file to copy.
   *
   * @param target Local file into which the remote file's contents will be
   * copied.
   *
   * @throws NullPointerException If either [source] or [target] is `null`.
   *
   * @throws IllegalArgumentException If [source] is blank.
   *
   * @throws FileNotFoundException If [source] was not found in this workspace.
   *
   * @throws S34KError If an error occurs in the underlying library while
   * communicating with the S3 server.
   */
  @Throws(
    NullPointerException::class,
    IllegalArgumentException::class,
    FileNotFoundException::class,
    S34KError::class
  )
  fun copy(source: String, target: File)

  /**
   * Recursively lists all the files in the remote 'directory'.
   *
   * The workspace marker file `.workspace` will not be included in this list.
   *
   * @return A list of absolute paths to files contained in this workspace.
   *
   * @throws S34KError If an error occurs in the underlying library while
   * communicating with the S3 server.
   */
  @Throws(S34KError::class)
  fun files(): List<String>

  /**
   * Tests whether this workspace contains the target file.
   *
   * @param path Relative path to the file to test for.
   *
   * @return `true` if the target path exists, otherwise `false`.
   *
   * @throws NullPointerException If [path] is `null`.
   *
   * @throws S34KError If an error occurs in the underlying library while
   * communicating with the S3 server.
   */
  @Throws(NullPointerException::class, S34KError::class)
  operator fun contains(path: String): Boolean

  /**
   * Recursively deletes this workspace and all of its contents.
   *
   * @throws S34KError If an error occurs in the underlying library while
   * communicating with the S3 server.
   */
  @Throws(S34KError::class)
  fun delete()

  /**
   * Tests whether this workspace contains a sub-workspace with the given ID.
   *
   * @param id ID of the sub-workspace to test for.
   *
   * @return `true` if the target sub-workspace exists, otherwise `false`
   *
   * @throws NullPointerException If [id] is `null`.
   *
   * @throws S34KError If an error occurs in the underlying library while
   * communicating with the S3 server.
   */
  @Throws(NullPointerException::class, S34KError::class)
  fun hasSubWorkspace(id: HashID): Boolean

  /**
   * Attempts to open the target workspace with the given [id].
   *
   * If no such workspace exists, this method returns `null`.
   *
   * @param id ID of the sub-workspace to attempt to open.
   *
   * @return Either the target sub-workspace, or `null`.
   *
   * @throws NullPointerException If [id] is `null`.
   *
   * @throws S34KError If an error occurs in the underlying library while
   * communicating with the S3 server.
   */
  @Throws(NullPointerException::class, S34KError::class)
  fun openSubWorkspace(id: HashID): SubWorkspace?

  /**
   * Creates a sub-workspace under this workspace with the given [id].
   *
   * @param id ID of the sub-workspace to create.
   *
   * @return A [SubWorkspace] instance wrapping the newly created sub-workspace.
   *
   * @throws NullPointerException If [id] is `null`.
   *
   * @throws IllegalStateException If the target sub-workspace already exists.
   *
   * @throws S34KError If an error occurs in the underlying library while
   * communicating with the S3 server.
   */
  @Throws(NullPointerException::class, S34KError::class)
  fun createSubWorkspace(id: HashID): SubWorkspace
}