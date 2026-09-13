#!/bin/sh
# Maven Wrapper launcher for the distribution configured in
# .mvn/wrapper/maven-wrapper.properties (only-script style).
set -eu

BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
PROPS="$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"

if [ ! -f "$PROPS" ]; then
  echo "Missing $PROPS" >&2
  exit 1
fi

DISTRIBUTION_URL=$(sed -n 's/^[[:space:]]*distributionUrl[[:space:]]*=[[:space:]]*//p' "$PROPS" | tail -n 1 | tr -d '\r')
if [ -z "$DISTRIBUTION_URL" ]; then
  echo "distributionUrl is not configured in $PROPS" >&2
  exit 1
fi

if [ -n "${MVNW_REPOURL:-}" ]; then
  DISTRIBUTION_URL="$MVNW_REPOURL/${DISTRIBUTION_URL##*/}"
fi

MAVEN_USER_HOME=${MAVEN_USER_HOME:-"${HOME}/.m2"}
DIST_FILE=${DISTRIBUTION_URL##*/}
DIST_NAME=${DIST_FILE%.zip}
DIST_NAME=${DIST_NAME%-bin}

if command -v sha256sum >/dev/null 2>&1; then
  URL_HASH=$(printf '%s' "$DISTRIBUTION_URL" | sha256sum | awk '{print $1}')
elif command -v shasum >/dev/null 2>&1; then
  URL_HASH=$(printf '%s' "$DISTRIBUTION_URL" | shasum -a 256 | awk '{print $1}')
else
  URL_HASH=$(printf '%s' "$DISTRIBUTION_URL" | cksum | awk '{print $1}')
fi

DIST_DIR="$MAVEN_USER_HOME/wrapper/dists/$DIST_NAME/$URL_HASH"
MAVEN_HOME="$DIST_DIR/$DIST_NAME"
MVN="$MAVEN_HOME/bin/mvn"

if [ ! -x "$MVN" ]; then
  mkdir -p "$DIST_DIR"
  TMP_ZIP="$DIST_DIR/$DIST_FILE.part"

  echo "Downloading Maven from $DISTRIBUTION_URL" >&2
  if command -v curl >/dev/null 2>&1; then
    if [ -n "${MVNW_USERNAME:-}" ] || [ -n "${MVNW_PASSWORD:-}" ]; then
      curl -fsSL -u "${MVNW_USERNAME:-}:${MVNW_PASSWORD:-}" "$DISTRIBUTION_URL" -o "$TMP_ZIP"
    else
      curl -fsSL "$DISTRIBUTION_URL" -o "$TMP_ZIP"
    fi
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$DISTRIBUTION_URL" -O "$TMP_ZIP"
  else
    echo "Neither curl nor wget is available to download Maven." >&2
    exit 1
  fi

  rm -rf "$MAVEN_HOME"
  unzip -q "$TMP_ZIP" -d "$DIST_DIR"
  rm -f "$TMP_ZIP"
fi

exec "$MVN" "$@"
