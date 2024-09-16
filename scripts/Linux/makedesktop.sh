#!/bin/sh

PACKAGE="ate"

DIR=$(dirname "$0")
DESKTOP="$DIR/ate.desktop"
ICON_DIR="$DIR/../MacOS/ate.iconset/"
PKG_DIR="$DIR/../../pkg/$PACKAGE"
ICON_DEST="$PKG_DIR/share/icons/hicolor"

printf "If you didn't yet run package.sh, expect issues\n"

printf "Creating applications dir\n"
mkdir -p $PKG_DIR/share/applications

printf "Copying desktop file\n"
cp $DESKTOP $PKG_DIR/share/applications/

printf "Creating icons dir\n"
mkdir -p $ICON_DEST

printf "Creating and copying 16x16 icons\n"
mkdir -p $ICON_DEST/16x16
cp $ICON_DIR/icon_16x16.png $ICON_DEST/16x16/$PACKAGE.png

printf "Creating and copying 32x32 icons\n"
mkdir -p $ICON_DEST/32x32
cp $ICON_DIR/icon_32x32.png $ICON_DEST/32x32/$PACKAGE.png

printf "Creating and copying 64x64 icons\n"
mkdir -p $ICON_DEST/64x64
cp "$ICON_DIR/icon_32x32@2x.png" $ICON_DEST/64x64/$PACKAGE.png

printf "Creating and copying 128x128 icons\n"
mkdir -p $ICON_DEST/128x128
cp $ICON_DIR/icon_128x128.png $ICON_DEST/128x128/$PACKAGE.png

# TODO: Maybe add support for scalable

