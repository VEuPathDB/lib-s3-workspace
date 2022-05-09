package org.veupathdb.lib.s3.workspaces

/**
 * # Sub-Workspace
 *
 * Represents a nested workspace for a job in which workspace specific files and
 * directories reside.
 *
 * A sub-workspace, like a [Workspace], may have sub-workspaces contained inside
 * it which are accessible via the [openSubWorkspace] method.
 *
 * @author Elizabeth Paige Harper [https://github.com/Foxcapades]
 * @since v1.0.0
 */
interface SubWorkspace : Workspace {

  /**
   * Parent workspace of this sub-workspace.
   *
   * The parent workspace may be a sub-workspace itself.
   */
  val parent: Workspace
}