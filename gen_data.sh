#!/bin/sh

DESCR="Generate sample data from a population"
USAGE="Usage: $0 sample_size population_file data_file"
if [ "$#" -lt 3 ]; then
	echo "$DESCR"
        echo "$USAGE"
        exit 1
fi

./rsplit $1 $2 $3 /dev/null
