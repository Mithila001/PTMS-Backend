#!/usr/bin/env sh
set -eu

: "${SPRING_PROFILES_ACTIVE:?SPRING_PROFILES_ACTIVE must be explicitly set (for example: prod)}"

exec java ${JAVA_OPTS:-} -Dspring.profiles.active="$SPRING_PROFILES_ACTIVE" -jar /app/app.jar
