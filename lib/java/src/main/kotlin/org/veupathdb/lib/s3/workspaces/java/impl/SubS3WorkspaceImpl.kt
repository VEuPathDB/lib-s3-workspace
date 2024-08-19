package org.veupathdb.lib.s3.workspaces.java.impl

import org.veupathdb.lib.s3.workspaces.java.SubS3Workspace
import org.veupathdb.lib.s3.workspaces.java.S3Workspace
import org.veupathdb.lib.s3.workspaces.S3Workspace as KTWorkspace

internal class SubS3WorkspaceImpl(
  delegate: KTWorkspace,
  override val parent: S3Workspace,
) : SubS3Workspace, S3WorkspaceImpl(delegate)