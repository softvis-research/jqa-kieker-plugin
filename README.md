# jQAssistant Kieker Plugin

[![GitHub license](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://github.com/softvis-research/jqa-kieker-plugin/blob/master/LICENSE)
[![Build Status](https://travis-ci.com/softvis-research/jqa-kieker-plugin.svg?branch=master)](https://travis-ci.com/softvis-research/jqa-kieker-plugin)

This is a [Kieker](https://github.com/kieker-monitoring/kieker) stack trace parser for [jQAssistant](https://jqassistant.org/). 
It enables jQAssistant to scan and to analyze data from [Kieker](https://github.com/kieker-monitoring/kieker).

## Getting Started

Download the jQAssistant command line tool for your system: [jQAssistant - Get Started](https://jqassistant.org/get-started/).

Next download the latest version from the release tab. Put the `jqa-kieker-plugin-*.jar` into the plugins folder 
of the jQAssistant commandline tool.

Now scan your configuration and wait for the plugin to finish:

```bash
jqassistant.sh scan -f
```

You can then start a local Neo4j server to start querying the database at [http://localhost:7474](http://localhost:7474):

```bash
jqassistant.sh server
```
## Publications

* Richard Müller and Tom Strempel: [Graph-Based Performance Analysis at System- and Application-Level](https://www.researchgate.net/publication/344609680_Graph-Based_Performance_Analysis_at_System-_and_Application-Level), 11th Symposium on Software Performance, Lepzig, Germany, 2020.
* Richard Müller and Matteo Fischer: [Graph-Based Analysis and Visualization of Software Traces](https://www.researchgate.net/publication/344608183_Graph-Based_Analysis_and_Visualization_of_Software_Traces), 10th Symposium on Software Performance, Würzburg, Germany, 2019.

You can find a complete list of publications on [our website](http://home.uni-leipzig.de/svis/Publications/).
