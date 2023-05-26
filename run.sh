#!/usr/bin/env bash
set -eu

javac net/kgtkr/seekprog/App.java

processing-java --sketch=$PWD/Pde --output=$PWD/PdeOut --force --build
cp PdeOut/*.class .
# mac only
classpath="."
for jar in /Applications/Processing.app/Contents/Java/*.jar; do
    classpath="$classpath:$jar"
done
for jar in /Applications/Processing.app/Contents/Java/core/library/*.jar; do
    classpath="$classpath:$jar"
done
for jar in /Applications/Processing.app/Contents/Java/modes/java/mode/*.jar; do
    classpath="$classpath:$jar"
done
java -cp "$classpath" net.kgtkr.seekprog.App
