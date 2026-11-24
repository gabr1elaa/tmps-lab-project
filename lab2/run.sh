#!/bin/bash

set -e
cd "$(dirname "$0")"

# Find a JDK: JAVA_HOME, then the macOS helper, then JDKs that IntelliJ downloaded.
if [ -z "$JAVA_HOME" ] && [ -x /usr/libexec/java_home ]; then
    JAVA_HOME=$(/usr/libexec/java_home 2>/dev/null || true)
fi
if [ -z "$JAVA_HOME" ]; then
    JAVA_HOME=$(ls -d "$HOME"/Library/Java/JavaVirtualMachines/*/Contents/Home 2>/dev/null | tail -1)
fi

if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
    JAVA="$JAVA_HOME/bin/java"
    JAVAC="$JAVA_HOME/bin/javac"
else
    JAVA=java
    JAVAC=javac
fi

# Compile the latest code, then start the app.
mkdir -p target/classes
"$JAVAC" -encoding UTF-8 -d target/classes $(find src/main/java -name '*.java')
"$JAVA" -cp target/classes com.mytasks.Main
