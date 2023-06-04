#!/usr/bin/env bash
set -eu

if [ "$(uname -sm)" = "Linux x86_64" ]; then
    PROCESSING_OS="linux"
    PROCESSING_ARCH=x64
    PROCESSING_ARC_EXT=tgz
elif [ "$(uname -sm)" = "Darwin arm64" ]; then
    PROCESSING_OS="macos"
    PROCESSING_ARCH=aarch64
    PROCESSING_ARC_EXT=zip
else
    echo "Unsupported OS: $(uname -s)"
    exit 1
fi

VERSION_NUMBER="1292"
VERSION="4.2"
REPO="processing4"
TAG="processing-${VERSION_NUMBER}-${VERSION}"
ASSET_NAME_WITHOUT_EXT="processing-${VERSION}-${PROCESSING_OS}-${PROCESSING_ARCH}"
ASSET_NAME="${ASSET_NAME_WITHOUT_EXT}.${PROCESSING_ARC_EXT}"
CACHE_DIR="cache"

mkdir -p "$CACHE_DIR"

CACHE_ARC="$CACHE_DIR/${ASSET_NAME}"
if [ ! -e "$CACHE_ARC" ]; then
    BIN_URL=https://github.com/processing/$REPO/releases/download/$TAG/$ASSET_NAME
    wget $BIN_URL -O $CACHE_ARC
fi

CACHE_EXPAND="$CACHE_DIR/$ASSET_NAME_WITHOUT_EXT"
if [ ! -e $CACHE_EXPAND ]; then
    mkdir $CACHE_EXPAND
    if [ "$PROCESSING_ARC_EXT" = "zip" ]; then
        unzip -d $CACHE_EXPAND $CACHE_ARC
    fi
    if [ "$PROCESSING_ARC_EXT" = "tgz" ]; then
        tar -xzf $CACHE_ARC -C $CACHE_EXPAND
    fi
fi


if [ "$PROCESSING_OS" = "macos" ]; then
    PROCESSING_ROOT=$CACHE_EXPAND/Processing.app/Contents/Java
else
    PROCESSING_ROOT=$CACHE_EXPAND/processing-$VERSION
fi

CLASSPATH_FILE="cache/$ASSET_NAME_WITHOUT_EXT-classpath.txt"
if [ "$PROCESSING_OS" = "macos" ]; then
    CLASSPATH_DIR_PREFIX=""
else
    CLASSPATH_DIR_PREFIX="lib/"
fi
if [ ! -e $CLASSPATH_FILE ]; then
    echo $PROCESSING_ROOT/${CLASSPATH_DIR_PREFIX}*.jar $PROCESSING_ROOT/core/library/*.jar $PROCESSING_ROOT/modes/java/mode/*.jar | tr ' ' ':' > $CLASSPATH_FILE
fi

ln -sf $PWD/$CLASSPATH_FILE $CACHE_DIR/classpath.txt
