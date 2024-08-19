package org.veupathdb.lib.s3.workspaces.java.impl

import java.io.File

import kotlinx.coroutines.runBlocking

import org.veupathdb.lib.s3.workspaces.java.WorkspaceFile
import org.veupathdb.lib.s3.workspaces.WorkspaceFile as KTFile

internal class WorkspaceFileImpl(private val delegate: KTFile) : WorkspaceFile {

  override val name
    get() = delegate.name

  override val absolutePath: String
    get() = delegate.absolutePath

  override val relativePath: String
    get() = delegate.relativePath

  override val lastModified
    get() = delegate.lastModified

  override fun size() = runBlocking { delegate.size() }

  override fun open() = runBlocking { delegate.open() }

  override fun download(localFile: File) = runBlocking { delegate.download(localFile) }

  override fun delete() = runBlocking { delegate.delete() }
}