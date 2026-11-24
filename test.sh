#!/bin/sh
# Compile and run the Deliverable 5 functional test suite. Results are written to test-results.tsv.
set -e
cd "$(dirname "$0")"
rm -rf out
javac -d out $(find src test -name "*.java")
java -cp out com.casos.FunctionalTests
