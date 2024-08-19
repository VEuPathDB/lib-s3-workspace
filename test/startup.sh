#!/usr/bin/env sh

while ! nc -z minio 80; do
  echo "waiting for minio"
  sleep 1
done

java -jar /test/build/libs/service.jar