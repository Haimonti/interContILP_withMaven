#!/bin/sh

DESCR="Generate arff files"
USAGE="Usage: $0 classes_file feature_file"

if [ "$#" -lt 2 ]; then
        echo "$DESCR"
        echo "$USAGE"
        exit 1
fi


/bin/rm train.[fn]
cat $1 | grep "pos" > train.f
cat $1 | grep "neg" | sed 's/neg/pos/' > train.n

./yap <<+
consult(aleph).
read_all(train).
consult('portray_features.pl').
consult('$2').
tell('train_pos.arff'), aleph_portray(train_pos), told.
tell('train_neg.arff'), aleph_portray(train_neg), told.
+

/bin/rm train.arff test.arff
mv train_pos.arff train.arff
cat train_neg.arff | grep -v ilp | grep -v relfeatures | grep -v class | grep -v data | grep -v ^$ >> train.arff
#mv test_pos.arff test.arff
# cat /tmp/test_neg.arff | grep -v ilp | grep -v relfeatures | grep -v class | grep -v data | grep -v ^$ >> test.arff

#/bin/rm *.arff
