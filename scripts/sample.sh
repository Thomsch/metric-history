#!/bin/bash

if [ $# != 3 ]; then
    echo "Missing arguments. Sample: <project name> <repo name> <n>"
    exit -1
fi

PROJECT_NAME=$1
REPO_NAME=$2
SAMPLE_SIZE=$3

ALL_ANCESTRY="$PROJECT_NAME-ancestry-all.csv"
RR_ANCESTRY="data/ancestry/$PROJECT_NAME-ancestry.csv"

# Get the ancestry for all revisions
metric-history/bin/metric-history ancestry "data/revision-lists/$PROJECT_NAME-revisions-all.csv" "repositories/$REPO_NAME/" $ALL_ANCESTRY

#Find all the revisions that are not refactoring revisions
comm -2 -3 <(sort $ALL_ANCESTRY) <(sort $RR_ANCESTRY) > difference.csv

#Get a random sample
shuf -n $SAMPLE_SIZE difference.csv > sample.csv

# Flatten the ancestry into a list
awk -F"," '{print $1}' sample.csv > sample-flat.txt
cp sample-flat.txt sample.txt
sed -i 's/[[:space:]]*$//' sample.txt

awk -F"," '{print $2}' sample.csv >> sample-flat.txt
sed -i 's/[[:space:]]*$//' sample-flat.txt
sort -u sample-flat.txt > temp.new && mv temp.new sample-flat.txt

mv sample.txt "$PROJECT_NAME-sample.txt"
mv sample-flat.txt "$PROJECT_NAME-sample-flat.txt"

# Clean up
rm difference.csv
rm sample.csv
