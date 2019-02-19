#!/usr/bin/env bash

##############################
# Flattens an ancestry file
##############################

if [ $# != 2 ]; then
    echo "Missing arguments. Sample: <ancestry file> <output>"
    exit -1
fi

# Replaces backslashes with forward slashes
INPUT=${1//\\//}
OUTPUT=${2//\\//}

tail -n +2 $INPUT > temp.csv
awk -F"," '{print $1}' temp.csv > flat.txt
awk -F"," '{print $2}' temp.csv >> flat.txt

sed -i 's/[[:space:]]*$//' flat.txt
sort -u flat.txt > temp.new && mv temp.new $OUTPUT

# Clean up
rm flat.txt
rm temp.csv