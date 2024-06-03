@file:Suppress("NOTHING_TO_INLINE")

package org.veupathdb.lib.s3.workspaces.util

private val validPathRgx = Regex("^/?(:?[A-Za-z\\d_\\-.]+/?)*\$")

internal inline fun String.validatePath() =
  if (!this.matches(validPathRgx))
    throw IllegalArgumentException("Invalid path value: $this")
  else
    this

internal inline fun String.extendPath(ext: String) =
  joinPaths(this, ext)

internal inline fun String.extendPath(ext1: String, ext2: String) =
  joinPaths(this, ext1, ext2)

internal inline fun String.toDirPath() =
  when {
    // If the input is blank, or is a root dir, return an empty prefix.
    isBlank() || this == "/"         -> ""
    // If the input string is an absolute path ending with a '/' character,
    // trim off the leading slash and return the rest.
    startsWith('/') && endsWith('/') -> substring(1)
    // If the input string already ends with a slash, return it.
    endsWith('/')                    -> this
    // If the input string begins with a slash, trim it off and append a
    // trailing '/' character.
    startsWith('/')                  -> substring(1) + "/"
    // If the input string does not end with a slash, append it and return.
    else                             -> "$this/"
  }

internal fun joinPaths(vararg segments: String): String {
  // Oversize our buffer a bit
  //
  // Also bundle in some validation on the first pass through to bail before we
  // create the buffer.
  val tmp = StringBuilder(segments.let {
    var out = 0
    it.forEach { s -> out += s.validatePath().length }
    out
  } + segments.size * 2)

  val last = segments.size - 1

  // Iterate over everything except the last element
  for (i in 0 until last) {
    // Skip blank elements and empty slashes
    if (segments[i].isBlank() || segments[i] == "/")
      continue

    if (segments[i].startsWith('/'))
      tmp.append(segments[i], 1, segments[i].length)
    else
      tmp.append(segments[i])

    if (!segments[i].endsWith('/'))
      tmp.append('/')
  }

  if (segments[last].startsWith('/'))
    tmp.append(segments[last], 1, segments[last].length)
  else
    tmp.append(segments[last])

  return tmp.toString()
}