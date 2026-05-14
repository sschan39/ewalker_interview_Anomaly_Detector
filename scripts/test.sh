#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
DEPS_DIR="$ROOT_DIR/.deps"
BUILD_DIR="$ROOT_DIR/.build"
JUNIT_JAR="$DEPS_DIR/junit-platform-console-standalone-1.10.2.jar"

mkdir -p "$DEPS_DIR" "$BUILD_DIR/classes" "$BUILD_DIR/test-classes"

if [[ ! -f "$JUNIT_JAR" ]]; then
  curl -fsSL -o "$JUNIT_JAR" \
    https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar
fi

find "$ROOT_DIR/src/main/java" -name '*.java' > "$BUILD_DIR/main-sources.txt"
find "$ROOT_DIR/src/test/java" -name '*.java' > "$BUILD_DIR/test-sources.txt"

javac -d "$BUILD_DIR/classes" @"$BUILD_DIR/main-sources.txt"
javac -cp "$JUNIT_JAR:$BUILD_DIR/classes" -d "$BUILD_DIR/test-classes" @"$BUILD_DIR/test-sources.txt"

java -jar "$JUNIT_JAR" \
  --class-path "$BUILD_DIR/classes:$BUILD_DIR/test-classes" \
  --scan-class-path