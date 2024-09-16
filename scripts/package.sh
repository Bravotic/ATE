#!/bin/sh

PACKAGE="ate"

OUTPUT_JARNAME="ATE.jar"

DIR=$(dirname $0)
PKG_DIR="$DIR/../pkg/$PACKAGE"
BIN_DIR="$PKG_DIR/bin"
LIB_DIR="$PKG_DIR/lib/ate"

printf "Removing package directory (if it exists)\n"
rm -r $PKG_DIR

printf "Creating bin directory\n"
mkdir -p $BIN_DIR

printf "Creating lib directory\n"
mkdir -p $LIB_DIR

printf "Moving jar to lib directory\n"
cp -r $DIR/../target/classes/* $LIB_DIR/

printf "Copying CLI start script to package\n"
cp $DIR/cli.sh $BIN_DIR/ate

printf "Copying GUI start script to package\n"
cp $DIR/gui.sh $BIN_DIR/gate

printf "Compressing package\n"
cd $DIR/../pkg
tar cvJf $PACKAGE.tar.xz $PACKAGE/