package org.veupathdb.lib.s3.workspaces.java.impl

import java.io.File
import java.io.InputStream

import kotlinx.coroutines.runBlocking

import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.s3.workspaces.S3Workspace as KTWorkspace
import org.veupathdb.lib.s3.workspaces.java.S3Workspace

internal open class S3WorkspaceImpl(private val delegate: KTWorkspace) : S3Workspace {
  override val id: HashID
    get() = delegate.id

  override val path: String
    get() = delegate.path

  override fun exists() =
    runBlocking { delegate.exists() }

  override fun touch(path: String) =
    runBlocking { WorkspaceFileImpl(delegate.touch(path)) }

  override fun get(path: String) =
    runBlocking { delegate.get(path)?.let(::WorkspaceFileImpl) }

  override fun write(path: String, stream: InputStream) =
    runBlocking { WorkspaceFileImpl(delegate.write(path, stream)) }

  override fun copy(from: File, to: String) =
    runBlocking { WorkspaceFileImpl(delegate.copy(from, to)) }

  override fun open(path: String) =
    runBlocking { delegate.open(path) }

  override fun copy(from: String, to: File) =
    runBlocking { delegate.copy(from, to) }

  override fun contains(path: String) =
    runBlocking { delegate.contains(path) }

  override fun files() =
    runBlocking { delegate.files().map(::WorkspaceFileImpl) }

  override fun delete() =
    runBlocking { delegate.delete() }

  override fun delete(path: String) =
    runBlocking { delegate.delete(path) }

  override fun hasSubWorkspace(id: HashID) =
    runBlocking { delegate.hasSubWorkspace(id) }

  override fun openSubWorkspace(id: HashID) =
    runBlocking { delegate.openSubWorkspace(id)?.let { SubS3WorkspaceImpl(it, this@S3WorkspaceImpl) } }

  override fun createSubWorkspace(id: HashID) =
    runBlocking { SubS3WorkspaceImpl(delegate.createSubWorkspace(id), this@S3WorkspaceImpl) }
}