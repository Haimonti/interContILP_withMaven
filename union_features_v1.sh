#!/bin/sh

DESCR="Generate union of feature files"
USAGE="Usage: $0 Infile1 Infile2 [Infile3 ...] Outfile"

if [ "$#" -lt 3 ]; then
	echo "$DESCR"
	echo "$USAGE"
	exit 1
fi

#/bin/rm features*.pl
cp $1 features.pl
shift

while [ "$#" -gt 1 ] 
do
yap <<+
consult('union_features_v1.pl').
tell('features_union.pl'), union('features.pl','$1'), told.
+
mv features_union.pl features.pl
shift
done
echo $1
cat features.pl | grep -v "features from " > $1
#/bin/rm features*.pl
