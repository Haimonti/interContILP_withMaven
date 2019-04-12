#!/bin/sh

DESCR="Generate pos and neg classes in data using target and background"
USAGE="Usage: $0 target_file data_file back_file classes_file"
if [ "$#" -lt 4 ]; then
        echo "$DESCR"
        echo "$USAGE"
        exit 1
fi

#/bin/rm classes.pl
./yap <<+
consult(aleph).
consult('$1').
consult('$2').
consult('$3').
consult('gen_classes.pl').
tell('classes.pl'), gen_classes, told.
+

mv classes.pl $4

