package org.veupathdb.lib.s3.workspaces.util

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

@DisplayName("string.kt")
class StringKtTest {

  @Nested
  @DisplayName("String.extendPath(String)")
  inner class ExtendPath1 {

    @Nested
    @DisplayName("Returns the input string")
    inner class Input {

      @Test
      @DisplayName("when the receiver string is empty")
      fun t1() {
        assertEquals("foo", "".extendPath("foo"))
      }

      @Test
      @DisplayName("when the receiver string is a single '/' character")
      fun t3() {
        assertEquals("foo", "/".extendPath("foo"))
      }
    }
  }

  @Nested
  @DisplayName("String.extendPath(String, String)")
  inner class ExtendPath2 {}

  @Nested
  @DisplayName("String.toDirPath()")
  inner class ToDirPath {

    @Nested
    @DisplayName("Returns an empty string")
    inner class Empty {

      @Test
      @DisplayName("when the receiver string is empty")
      fun t1() {
        assertEquals("", "".toDirPath())
      }

      @Test
      @DisplayName("when the receiver string is blank")
      fun t2() {
        assertEquals("", "     ".toDirPath())
      }

      @Test
      @DisplayName("when the receiver string is '/'")
      fun t3() {
        assertEquals("", "/".toDirPath())
      }
    }

    @Nested
    @DisplayName("Returns the receiver string")
    inner class Same {

      @Test
      @DisplayName("when the receiver string ends with a '/' character")
      fun t1() {
        assertEquals("foo/", "foo/".toDirPath())
      }
    }

    @Nested
    @DisplayName("Returns a string with the leading slash trimmed off")
    inner class TrimLeft {

      @Test
      @DisplayName("when the receiver string begins with a '/' character")
      fun t1() {
        assertEquals("foo/", "/foo".toDirPath())
      }

      @Test
      @DisplayName("when the receiver string begins and ends with a '/' character")
      fun t2() {
        assertEquals("foo/", "/foo/".toDirPath())
      }
    }

    @Nested
    @DisplayName("Returns the input string with a trailing '/'")
    inner class Append {

      @Test
      @DisplayName("when the receiver string does not end with a '/' character")
      fun t1() {
        assertEquals("foo/", "foo".toDirPath())
      }
    }
  }

  @Nested
  @DisplayName("joinPaths(String...)")
  inner class JoinPaths {

    @Nested
    @DisplayName("Throws IllegalArgumentException")
    inner class ThrowsIAE {

      @Test
      @DisplayName("when an input string is not a valid path segment")
      fun t1() {
        assertThrows<IllegalArgumentException> {
          joinPaths("foo", "b a r")
        }
      }
    }

    @Nested
    @DisplayName("trims out extra slashes in the input params")
    inner class TrimsSlashes {

      @Test
      @DisplayName("when the input strings contain leading/trailing slashes")
      fun t1() {
        assertEquals("foo/bar/", joinPaths("/foo/", "/bar/"))
      }

      @Test
      @DisplayName("when the input strings contain single '/' strings")
      fun t2() {
        assertEquals("foo/bar", joinPaths("/", "/foo/", "/", "/bar"))
      }

      @Test
      @DisplayName("when the input strings contain empty strings")
      fun t3() {
        assertEquals("foo/bar", joinPaths("", "/foo", "", "bar"))
      }
    }

    @Nested
    inner class Join {

      @Test
      fun t1() {
        assertEquals("foo/.workspace", joinPaths("foo", ".workspace"))
      }
    }
  }
}