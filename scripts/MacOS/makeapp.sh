#!/bin/sh

DIR=$(dirname "$0")
PACKAGE="Ate"

BUNDLE="$DIR/../../pkg/$PACKAGE.app"

ICONSET="$DIR/ate.iconset"
INFO_PLIST="$DIR/Info.plist"
EXEC=$DIR/apprunner.sh

printf "Removing app bundle (if it exists)\n"
rm -r $BUNDLE

printf "Making bundle\n"
mkdir -p $BUNDLE
mkdir -p $BUNDLE/Contents/MacOS
mkdir -p $BUNDLE/Contents/Resources
mkdir -p $BUNDLE/Contents/lib/ate/

printf "Creating icns from iconset\n"
iconutil -c icns $ICONSET --output $BUNDLE/Contents/Resources/$PACKAGE.icns

printf "Copying Info.plist file\n"
cp $INFO_PLIST $BUNDLE/Contents/

printf "Copying executable\n"
cp $EXEC $BUNDLE/Contents/MacOS/$PACKAGE

printf "Copying lib directoy\n"
cp -r $DIR/../../target/classes/* $BUNDLE/Contents/lib/ate/
