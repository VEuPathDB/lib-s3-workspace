package org.veupathdb.lib.s3.workspaces.impl

import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.workspaces.SubS3Workspace
import org.veupathdb.lib.s3.workspaces.S3Workspace

internal class SubS3WorkspaceImpl(
  id: HashID,
  path: String,
  override val parent: S3Workspace,
  s3: S3Bucket,
) : SubS3Workspace, S3WorkspaceImpl(id, path, s3)