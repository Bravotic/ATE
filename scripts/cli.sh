#!/bin/sh

stty -echo -icanon
java -cp $(dirname "$0")/../lib/ate com.bravotic.ate.vtlib.ATextEditorVT100 $@
stty echo icanon