[![Build Status](https://travis-ci.com/Thomsch/metric-history.svg?token=kEZ3SvFYosMEzwAWUkVz&branch=master)](https://travis-ci.com/Thomsch/metric-history)
[![Maintainability](https://api.codeclimate.com/v1/badges/f54676e2d0e9d7a5f871/maintainability)](https://codeclimate.com/github/Thomsch/metric-history/maintainability)

# MetricHistory
MetricHistory is an extensible tool designed to collect and process software
measurements across multiple versions of a code base. The measurement itself is
modular and executed by a third party tool. The default analyzer is
[SourceMeter](https://www.sourcemeter.com/) which offers more than 52 metrics at
the project, package, class, and method level.

**Features:**

- Automated collection of measurements for multiple version of a project.
- Computation of the difference in metrics between two version of a project.

On top of these features, MetricHistory offers a collection of optional
utilities:

- Conversion of native analysis results into a easily readable reference format (RAW format).
- Retrieval of the parent versions of a list of versions.
- [Incubating] Exports to mongodb database.

## Installation
Download the latest release at https://github.com/Thomsch/metric-history/releases. Unzip it and run `./bin/metric-history -V` to check if your installation is good. This instruction should print the current version.
If you plan on using Sourcemeter as the analyzer, you need to install it beforehand from the vendor's website.

## Usage
### Running from command line
MetricHistory comes with a rich command line interface. Every command is explained in the `help` command (`./metric-history help`).

#### Example - Collecting the metrics of a list of versions with SourceMeter
`./metric-history collect versions.txt path/to/repository/ output/folder/ SOURCEMETER -e=path/to/sourcemeter/executable` 
This command analyzes the versions given in _versions.txt_ of the project located in _repository_ using the analyzer
'Sourcemeter'. _versions.txt_ contains a list of commit ids (in case of a git project). The results are stored in
`output/folder`.

### Using the API
You can also choose to integrate metric history to **your** projects by using its public API. Inspire yourself from the implementations in `org.metrichistory.cmd.*`: they all use the public API!

## Roadmap
- Seamless support of databases
- Support for more software measurement providers (i.e., code smells)
- Support for different levels of granularity based on the measurement provider

## Contributing
Suggestions are welcomed! Try creating a new issue or submitting a pull request!

### Building
We use [Gradle](https://gradle.org/) to manage dependencies and compile the project the project.

Run `./gradlew build` (or `./gradlew.bat ...` if you're on Windows) to build the project. Dependencies will be download automatically.

Run `./gradlew installDist` to install the application locally.

Run `./gradlew distZip` to create a full distribution ZIP archive including runtime libraries and OS specific scripts.

### Testing
Run `./gradlew test` to execute the tests.
