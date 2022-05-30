package org.veupathdb.lib.s3.workspaces.impl

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.S3Object
import org.veupathdb.lib.s3.workspaces.WorkspaceFile
import java.io.File
import java.io.InputStream

internal class WorkspaceFileImpl(
  override val path: String,
  private val root: S3Object,
  private val buck: S3Bucket,
) : WorkspaceFile {
  override val lastModified
    get() = root.lastModified

  override val size by lazy { root.stat()!!.size }

  override val name by lazy { path.substring(path.lastIndexOf('/')+1) }

  override fun open(): InputStream {
    return buck.objects.open(path)!!.stream
  }

  override fun download(localFile: File): File {
    return buck.objects.download(path, localFile).localFile
  }

  override fun delete() {
    root.delete()
  }
}