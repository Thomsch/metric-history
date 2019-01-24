[![Build Status](https://travis-ci.com/Thomsch/metric-history.svg?token=kEZ3SvFYosMEzwAWUkVz&branch=master)](https://travis-ci.com/Thomsch/metric-history)
# Metric history
Metric History is a multi-purpose tool for capturing and processing metrics or other attributes from
multiple revisions of a code base. The project analysis can be delegated to a third party tool.
The default implementation uses [SourceMeter](https://www.sourcemeter.com/).

Features:
* Automatic analysis of multiple versions of a project.
* Conversion of native analysis results into a easily readable reference format (RAW format).
* Computation of the fluctuations of attributes for selected revisions and export into CSV.
* Accessible retrieval of the parents version of a list of revisions.
* Retrieve analysis results for a version directly in RAW format.
* Exports to mongodb database [incubating feature]

These features are accessible from the command line or the API.

## Running from command line
`./metric-history collect versions.csv analysis-software.exe myproject/ results/ myproject`
This command will analyse all the versions (and their parents) of _myproject_ using the 
analysis software specified (_analysis-software.exe_) specified in _version.csv_ which contains simply a list of
commit ids for a GIT based project and store the results in the specified folder (_results/_) under a directory with
the name of the project (_myproject_).

## Using the API
Inspire yourself from the commands in `ch.thomsch.cmd.*`, they all use the public API!

## Building
This projects uses Gradle to get all the dependencies and build itself. You're also free to use the native
compiler of your IDE if you wish.

## Contributing
Suggestions are welcomed! Try creating a new issue or submitting a pull request!
