#!/usr/bin/env bash
set -eu

VERSION_NUMBER="1292"
VERSION="4.2"
REPO="processing4"
TAG="processing-${VERSION_NUMBER}-${VERSION}"
ASSET_NAME="processing-${VERSION}-linux-x64.tgz"
CACHE_DIR="cache"

mkdir -p "$CACHE_DIR"

CACHE_TGZ="$CACHE_DIR/$TAG.tgz"
if [ ! -e "$CACHE_TGZ" ]; then
    BIN_URL=https://github.com/processing/$REPO/releases/download/$TAG/$ASSET_NAME
    wget $BIN_URL -O $CACHE_TGZ
fi

CACHE_EXPAND="$CACHE_DIR/$TAG"
if [ ! -e $CACHE_EXPAND ]; then
    mkdir $CACHE_EXPAND
    tar -xzf $CACHE_TGZ -C $CACHE_EXPAND
fi

CACHE_JARS="$CACHE_DIR/$TAG-jars"
if [ ! -e $CACHE_JARS ]; then
    mkdir $CACHE_JARS

    PDE_ROOT="$CACHE_EXPAND/processing-$VERSION"

    cp $PDE_ROOT/core/library/*.jar $PDE_ROOT/modes/java/mode/*.jar $PDE_ROOT/lib/*.jar $CACHE_JARS
fi

ln -sf $PWD/$CACHE_JARS lib
