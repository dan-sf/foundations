#!/usr/bin/env bash

set -e

# This script creates some test data, compiles the mapreduce code, creates a
# jar, and runs that jar with hadoop

# The mapreduce code assumes to be run where this script lives so we pushd to
# the scripts path here
current_dir=`cd $(dirname $0) && pwd`
pushd $current_dir > /dev/null

# Creat test data
echo "Creating test input data"
mkdir -p input
echo "hello world one two three hello hello two" > input/input.txt

echo
echo ----------
echo INPUT DATA
echo ----------
echo
cat input/input.txt
echo

mkdir -p classes

# Remove output dir if it exists
[ -d output ] && rm -r output

echo "Compiling the java code"
javac -classpath ".:`$HADOOP_HOME/bin/hadoop classpath`" -d classes BasicWordCount.java

echo "Creating jar file"
jar -cvf classes/BasicWordCount.jar classes

echo "Running mapreduce in standalone mode"
${HADOOP_HOME}/bin/hadoop jar classes/BasicWordCount.jar BasicWordCount

echo
echo -----------
echo OUTPUT DATA
echo -----------
echo
cat output/*
echo

# Cleanup
rm -r input output classes

# popd back to original run dir
popd > /dev/null

