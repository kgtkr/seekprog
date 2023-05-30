#!/usr/bin/env bash
set -eu

classpath=""

if [ "$(uname)" == 'Linux' ]; then
    BASEDIR=$(dirname $(which processing-java))
    for jar in $BASEDIR/lib/*.jar; do
        classpath="$classpath:$jar"
    done
    for jar in $BASEDIR/core/library/*.jar; do
        classpath="$classpath:$jar"
    done
    for jar in $BASEDIR/modes/java/mode/*.jar; do
        classpath="$classpath:$jar"
    done
fi

if [ "$(uname)" == 'Darwin' ]; then
    for jar in /Applications/Processing.app/Contents/Java/*.jar; do
        classpath="$classpath:$jar"
    done
    for jar in /Applications/Processing.app/Contents/Java/core/library/*.jar; do
        classpath="$classpath:$jar"
    done
    for jar in /Applications/Processing.app/Contents/Java/modes/java/mode/*.jar; do
        classpath="$classpath:$jar"
    done
fi

javac -cp "$classpath" src/net/kgtkr/seekprog/*.java

processing-java --sketch=$PWD/Pde --output=$PWD/PdeOut --force --build

jar cvf seekprog.jar -C src .
jar cvf PdeOut.jar -C PdeOut .

java -cp "$PWD/seekprog.jar:$PWD/PdeOut.jar:$classpath" net.kgtkr.seekprog.App
