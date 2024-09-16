#!/bin/sh

cd ~
java -Xdock:icon=$(dirname "$0")/../lib/ate/com/bravotic/ate/icon.png \
	 -Xdock:name="A Text Editor" \
	 -cp $(dirname "$0")/../lib/ate com.bravotic.ate.swing.ATESwingView \
	 $@


