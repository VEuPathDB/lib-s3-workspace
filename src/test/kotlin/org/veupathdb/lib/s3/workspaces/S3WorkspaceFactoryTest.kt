package org.veupathdb.lib.s3.workspaces

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.s3.s34k.S3Client
import org.veupathdb.lib.s3.s34k.buckets.BucketContainer
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import org.veupathdb.lib.s3.s34k.objects.S3Object
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DisplayName("WorkspaceFactory")
internal class S3WorkspaceFactoryTest {

  private val provisionClient: S3Client get() = mock(S3Client::class.java)

  @Nested
  @DisplayName("::init")
  inner class Init {

    @Nested
    @DisplayName("Throws IllegalArgumentException")
    inner class ThrowsIAE {

      @Test
      @DisplayName("when the input bucket name is not a valid S3 bucket name")
      fun t1() {

        assertThrows<IllegalArgumentException> {
          S3WorkspaceFactory(provisionClient, "hello world")
        }
      }
    }

    @Nested
    @DisplayName("Throws IllegalStateException")
    inner class ThrowsISE {

      @Test
      @DisplayName("when the target bucket does not exist")
      fun t1() {
        val client  = provisionClient
        val buckets = mock(BucketContainer::class.java)

        `when`(client.buckets).thenReturn(buckets)
        `when`(buckets.exists(BucketName("hello-world"))).thenReturn(false)

        assertThrows<IllegalStateException> {
          S3WorkspaceFactory(client, "hello-world")
        }
      }
    }
  }

  @Nested
  @DisplayName("#get(HashID)")
  inner class Get {

    @Nested
    @DisplayName("Returns null")
    inner class RNull {

      @Test
      @DisplayName("when the target workspace does not exist.")
      fun t1() {
        val name    = BucketName("hello-world")
        val client  = provisionClient
        val buckets = mock(BucketContainer::class.java)
        val objects = mock(ObjectContainer::class.java)
        val bucket  = mock(S3Bucket::class.java)

        val id = HashID("12345678901234561234567890123456")

        `when`(client.buckets).thenReturn(buckets)
        `when`(buckets.exists(name)).thenReturn(true)
        `when`(buckets[name]).thenReturn(bucket)
        `when`(bucket.objects).thenReturn(objects)
        `when`(objects.contains("12345678901234561234567890123456/.workspace")).thenReturn(false)

        assertNull(S3WorkspaceFactory(client, "hello-world")[id])
      }
    }

    @Nested
    @DisplayName("Returns a Workspace instance")
    inner class RWorkspace {

      @Test
      @DisplayName("when the target workspace exists")
      fun t1() {
        val name    = BucketName("goodbye-world")
        val client  = provisionClient
        val buckets = mock(BucketContainer::class.java)
        val objects = mock(ObjectContainer::class.java)
        val bucket  = mock(S3Bucket::class.java)
        val hash    = HashID("12345678901234561234567890123456")

        `when`(client.buckets).thenReturn(buckets)
        `when`(buckets.exists(name)).thenReturn(true)
        `when`(buckets[name]).thenReturn(bucket)
        `when`(bucket.objects).thenReturn(objects)
        `when`(objects.contains("12345678901234561234567890123456/.workspace")).thenReturn(true)

        assertNotNull(S3WorkspaceFactory(client, name.name)[hash])
      }
    }
  }

  @Nested
  @DisplayName("#create(HashID)")
  inner class Create {

    @Nested
    @DisplayName("Throws WorkspaceAlreadyExistsError")
    inner class ThrowsWAEE {

      @Test
      @DisplayName("when the target workspace already exists")
      fun t1() {
        val client  = provisionClient
        val bucket  = mock(S3Bucket::class.java)
        val buckets = mock(BucketContainer::class.java)
        val objects = mock(ObjectContainer::class.java)
        val name    = BucketName("waka-waka-waka")
        val hash    = HashID("12345678901234561234567890123456")

        `when`(client.buckets).thenReturn(buckets)
        `when`(buckets.exists(name)).thenReturn(true)
        `when`(buckets.exists(name)).thenReturn(true)
        `when`(buckets[name]).thenReturn(bucket)
        `when`(bucket.objects).thenReturn(objects)
        `when`(objects.contains("12345678901234561234567890123456/.workspace")).thenReturn(true)

        assertThrows<WorkspaceAlreadyExistsError> {
          S3WorkspaceFactory(client, name.name).create(hash)
        }
      }
    }

    @Nested
    @DisplayName("Returns a workspace instance")
    inner class RWorkspace {

      @Test
      @DisplayName("when the target workspace does not already exist")
      fun t1() {
        val name    = BucketName("taco-smell")
        val client  = provisionClient
        val buckets = mock(BucketContainer::class.java)
        val objects = mock(ObjectContainer::class.java)
        val bucket  = mock(S3Bucket::class.java)
        val derps   = mock(S3Object::class.java)
        val hash    = HashID("12345678901234561234567890123456")

        `when`(client.buckets).thenReturn(buckets)
        `when`(buckets.exists(name)).thenReturn(true)
        `when`(buckets[name]).thenReturn(bucket)
        `when`(bucket.objects).thenReturn(objects)
        `when`(objects.contains("12345678901234561234567890123456/.workspace")).thenReturn(false)
        `when`(objects.touch("12345678901234561234567890123456/.workspace")).thenReturn(derps)

        assertNotNull(S3WorkspaceFactory(client, name.name).create(hash))
      }
    }
  }
}