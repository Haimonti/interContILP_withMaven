#!/bin/sh

DESCR="Generate first-order features by sampling"
USAGE="Usage: $0 sample_size feature_file"

if [ "$#" -lt 2 ]; then
        echo "$DESCR"
        echo "$USAGE"
        exit 1
fi

/bin/rm train.[fn]
cat classes_1500c.pl | sed 's/neg/pos/' > train.f
/bin/rm features
./yap <<+
consult(aleph).
read_all(train).
consult('gen_features.pl').
rand_gen_features(any,$1).
tell('features'), show(features), told.
+
cat features | grep -v "features from" > $2
