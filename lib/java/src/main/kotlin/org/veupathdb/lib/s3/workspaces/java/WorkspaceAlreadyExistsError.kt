package org.veupathdb.lib.s3.workspaces.java

import org.veupathdb.lib.hash_id.HashID

/**
 * # Workspace Already Exists Error
 *
 * Exception thrown when attempting to create a workspace when that workspace
 * already exists in the S3 store.
 */
class WorkspaceAlreadyExistsError : Exception {

  /**
   * ID of the workspace whose creation was attempted.
   */
  val id: HashID

  constructor(id: HashID) : super(err(id)) {
    this.id = id
  }

  constructor(id: HashID, msg: String) : super(msg) {
    this.id = id
  }

  constructor(id: HashID, cause: Throwable) : super(err(id), cause) {
    this.id = id
  }

  constructor(id: HashID, msg: String, cause: Throwable) : super(msg, cause) {
    this.id = id
  }
}

@Suppress("NOTHING_TO_INLINE")
private inline fun err(id: HashID) =
  "Could not create a workspace for $id as a workspace already exists with that ID"