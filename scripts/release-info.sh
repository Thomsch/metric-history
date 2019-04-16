#!/bin/bash
projects=("ant" "xercesj" "dagger2" "hibernate-orm" "jena" "jmeter" "junit4" "okhttp" "retrofit" "rxjava")
METRIC_HISTORY=.
OUTPUT_ROOT=$2
LEVEL=patch
PROJECTS_DIR=$1

if [ $# != 3 ]; then
    echo "Produces release info for all projects <projectsDir> to output location <ouputDir> at level <level>"
    echo "Missing arguments. Sample: <projectsDir> <outputDir> <level>"
    exit -1
fi

if [ $3 == "minor" ]; then
  LEVEL=minor
fi

# $1 project name
# $2 default branch
function releaseInfo {
  $METRIC_HISTORY/gradlew run --args="revision-history $PROJECTS_DIR/$1 $OUTPUT_ROOT/tags/$LEVEL/$1.txt $2 $OUTPUT_ROOT/$LEVEL/$1.csv" 
}

releaseInfo ant master
releaseInfo xercesj trunk
releaseInfo dagger2 master
releaseInfo hibernate-orm master
releaseInfo jena master
releaseInfo jmeter trunk
releaseInfo junit4 master
releaseInfo okhttp master
releaseInfo retrofit master
releaseInfo rxjava 2.x
