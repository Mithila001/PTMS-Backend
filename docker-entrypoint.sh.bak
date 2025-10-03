#!/usr/bin/env bash
set -euo pipefail

echo "=== ENTRYPOINT: dump env (masked) ==="
echo "SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-<unset>}"
echo "DB_URL=${DB_URL:-<unset>}"
echo "DB_USERNAME=${DB_USERNAME:-<unset>}"
# don't print DB_PASSWORD

# optional wait for postgres if host is 'postgres'
if [ "${WAIT_FOR_POSTGRES:-true}" = "true" ]; then
  if command -v nc >/dev/null 2>&1; then
    echo "Waiting for postgres host: ${DB_HOST:-postgres}:5432"
    for i in $(seq 1 30); do
      nc -z -w 3 "${DB_HOST:-postgres}" 5432 && break
      echo "postgres not ready yet ($i)..."
      sleep 1
    done
  fi
fi

echo "Starting JVM..."
exec java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar /app/app.jar
