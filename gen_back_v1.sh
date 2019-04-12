#!/bin/sh

DESCR="Generate background file back_file from modes, determs and definitions"
USAGE="Usage: $0 mode_file determs_file defns_file back_file"

if [ "$#" -lt 4 ]; then
        echo "$DESCR"
        echo "$USAGE"
        exit 1
fi

modesfile=$1
determsfile=$2
defsfile=$3

backfile=$4

cat $modesfile $determsfile $defsfile > $backfile

/bin/rm background.pl 
ln -s $backfile background.pl

