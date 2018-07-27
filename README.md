[![Build Status](https://travis-ci.com/Thomsch/metric-history.svg?branch=master)](https://travis-ci.com/Thomsch/metric-history)
# Metric history
Metric History extracts metrics for multiple revisions of a project. Other functionalities include data transformation (computation of the changes in metrics) and creation of ancestry table.
The tool calls directly the command line version of SourceMeter. It can be easily modified to connect any third party command line tool.

Features:
* Computation of metrics for multiple revision either with CKMetrics or SourceMeter
* Conversion of SourceMeter results to raw format (raw-format) in CSV
* Aggregation of raw format to the list of changes in metrics for each class for each revision in CSV file (raw-changes)
* Generation of list of parents for each revision (ancestry) in CSV

## Examples
The files in `/src/test/java/ch/thomsch/example` contains examples of how to use the library. You **need** to have cloned the repositories for it to work.

## Usage
`./metric-history <command> <parameters...>`

Where `<command>` is one of:
* collect
* convert
* ancestry
* diff
* mongo

Use `./metric-history <command>` to learn more about a command !

## Building
This projects uses Gradle to get all the dependencies. Depending on your IDE, you can compile/run/test through Gradle or your IDE. It's up to you.

Suggestions are welcomed !
