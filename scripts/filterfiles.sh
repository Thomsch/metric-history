#!/usr/bin/env bash

if [ $# != 3 ]; then
	echo "Filters revisions in <file> from <dir> in <output>"
	echo "Missing files are listed in <output>/missing.txt"
    echo "Missing arguments. Sample: <file> <dir> <output>"
    exit -1
fi

# Replaces backslashes with forward slashes
DIR=${2//\\//}
FILE=${1//\\//}
OUTPUT=${3//\\//}

DIR=${DIR%/}

mkdir -p $OUTPUT
rm -f "$OUTPUT/missing.txt"

while IFS="" read -r p || [ -n "$p" ]
do
  filename="$p.csv"
  cp -f "$DIR/$filename" "$OUTPUT/$filename"
  
  if [ $? -ne 0 ]; then
    echo "$filename" >> "$OUTPUT/missing.txt"
  fi
  
done < $FILE

exit 0
