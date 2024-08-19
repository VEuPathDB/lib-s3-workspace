FROM amazoncorretto:16-alpine-jdk

ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

# Cache gradle so repeated builds don't take so long
COPY [ "settings.gradle.kts", "gradlew", "./" ]
COPY gradle gradle/

RUN ./gradlew tasks > /dev/null

# Project build
COPY [ "build.gradle.kts", "test/startup.sh", "./" ]
COPY lib lib/
COPY test test/

RUN ./gradlew :test:shadowJar

CMD ./startup.sh
