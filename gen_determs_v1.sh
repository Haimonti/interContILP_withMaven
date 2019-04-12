#!/bin/sh

DESCR="Generate random selection of determinations"
USAGE="Usage: $0 selectflag all_determs_file determs_file"

if [ "$#" -lt 3 ]; then
        echo "$DESCR"
        echo "$USAGE"
        exit 1
fi

# no random selection: simply copy
if [ "$1" -eq 0 ]; then
	cp $2 $3
	exit 0
fi

MAX=`wc -l $2 | awk '{print $1}'`
# RANDOM=$$
R=$(($(($RANDOM%MAX))+1))
#R=`shuf -i 1-$MAX -n 1`

# remove necessary determinations
cat $2 | grep -v "has_car" > determs.pl

# randomly select amongst rest
./rsplit $R determs.pl $3 /dev/null

# add necessary determinations
cat $2 | grep "has_car" >> $3

#/bin/rm /tmp/all_determs.pl
