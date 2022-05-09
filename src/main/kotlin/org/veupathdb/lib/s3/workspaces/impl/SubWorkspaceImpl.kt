package org.veupathdb.lib.s3.workspaces.impl

import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.workspaces.SubWorkspace
import org.veupathdb.lib.s3.workspaces.Workspace

internal class SubWorkspaceImpl(
  id: HashID,
  override val parent: Workspace,
  s3: S3Bucket,
  path: String,
) : SubWorkspace, WorkspaceImpl(id, s3, path)