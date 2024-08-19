package org.veupathdb.lib.s3.workspaces.impl

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.S3Object
import org.veupathdb.lib.s3.workspaces.WorkspaceFile
import java.io.File

internal class WorkspaceFileImpl(
  override val relativePath: String,
  private val root: S3Object,
  private val buck: S3Bucket,
) : WorkspaceFile {
  override val name by lazy { relativePath.substring(relativePath.lastIndexOf('/')+1) }

  override val absolutePath: String
    get() = root.path

  override val lastModified
    get() = root.lastModified

  override suspend fun size(): Long =
    root.stat()!!.size

  override suspend fun open() =
    buck.objects.open(absolutePath)!!.stream

  override suspend fun download(localFile: File) =
    buck.objects.download(absolutePath, localFile).localFile

  override suspend fun delete() =
    root.delete()
}