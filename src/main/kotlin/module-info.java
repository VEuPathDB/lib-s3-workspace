module s3.workspaces.main {
  requires kotlin.stdlib;
  requires kotlin.stdlib.common;
  requires kotlin.stdlib.jdk7;
  requires kotlin.stdlib.jdk8;
  requires org.slf4j;
  requires s34k;
  requires s34k.minio;
  requires hash.id;

  exports org.veupathdb.lib.s3.workspaces;
}