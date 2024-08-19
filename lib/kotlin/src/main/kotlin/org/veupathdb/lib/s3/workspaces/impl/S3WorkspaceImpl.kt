package org.veupathdb.lib.s3.workspaces.impl

import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.errors.ObjectNotFoundError
import org.veupathdb.lib.s3.workspaces.SubS3Workspace
import org.veupathdb.lib.s3.workspaces.S3Workspace
import org.veupathdb.lib.s3.workspaces.WorkspaceFile
import org.veupathdb.lib.s3.workspaces.util.extendPath
import org.veupathdb.lib.s3.workspaces.util.toDirPath
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

internal open class S3WorkspaceImpl(
  override val id: HashID,
  override val path: String,
  private val s3: S3Bucket,
) : S3Workspace {
  override suspend fun exists() = path.extendPath(MarkerFile) in s3.objects

  override suspend fun touch(path: String): WorkspaceFile =
    this.path.extendPath(path).let { WorkspaceFileImpl(it, s3.objects.touch(it), s3) }

  override suspend fun get(path: String): WorkspaceFile? =
    this.path.extendPath(path).let { p -> s3.objects[p]?.let { WorkspaceFileImpl(p, it, s3) } }

  override suspend fun write(path: String, stream: InputStream) : WorkspaceFile =
    this.path.extendPath(path).let { WorkspaceFileImpl(it, s3.objects.put(it, stream), s3) }

  override suspend fun copy(from: File, to: String): WorkspaceFile =
    this.path.extendPath(to).let { WorkspaceFileImpl(to, s3.objects.upload(it, from), s3) }

  override suspend fun open(path: String): InputStream =
    this.path.extendPath(path).let {
      s3.objects.open(path)?.stream ?: throw FileNotFoundException("Remote file $path was not found")
    }

  override suspend fun copy(from: String, to: File) {
    val path = path.extendPath(from)
    try {
      s3.objects.download(path, to)
    } catch (e: ObjectNotFoundError) {
      throw FileNotFoundException("Remote file $path was not found")
    }
  }

  override suspend fun contains(path: String) =
    s3.objects.contains(this.path.extendPath(path))

  override suspend fun files(): List<WorkspaceFile> {
    val dirPath = path.toDirPath()
    val rawList = s3.objects.list(dirPath)
    val outList = ArrayList<WorkspaceFile>(rawList.size - 1)

    rawList.forEach {
      if (!it.path.endsWith(".workspace")) {
        outList.add(WorkspaceFileImpl(it.path.substring(dirPath.length), it, s3))
      }
    }

   return outList
  }

  override suspend fun delete() =
    s3.objects.rmdir(path.toDirPath())

  override suspend fun delete(path: String) =
    s3.objects.delete(this.path.extendPath(path))

  override suspend fun hasSubWorkspace(id: HashID) =
    s3.objects.contains(path.extendPath(id.string, MarkerFile))

  override suspend fun openSubWorkspace(id: HashID) =
    if (s3.objects.contains(path.extendPath(id.string, MarkerFile)))
      SubS3WorkspaceImpl(id, path.extendPath(id.string), this, s3)
    else
      null

  override suspend fun createSubWorkspace(id: HashID): SubS3Workspace {
    val tgt = path.extendPath(id.string, MarkerFile)
    if (s3.objects.contains(tgt))
      throw IllegalStateException("Cannot create a sub-workspace for $id under $path.  A workspace already exists with this ID.")

    s3.objects.touch(tgt)
    return SubS3WorkspaceImpl(id, path.extendPath(id.string), this, s3)
  }
}