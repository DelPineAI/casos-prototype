#!/bin/sh
# Compile and start the Casos prototype at http://localhost:8080 (requires JDK 17 or newer).
set -e
cd "$(dirname "$0")"
rm -rf out
javac -d out $(find src -name "*.java")
java -cp out com.casos.app.CasosApplication
