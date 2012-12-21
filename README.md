# JSF Content Assist for spring beans in eclipse

[![Build Status](https://buildhive.cloudbees.com/job/Seitenbau/job/eclipse.plugin.sts-jsf-ca/badge/icon)](https://buildhive.cloudbees.com/job/Seitenbau/job/eclipse.plugin.sts-jsf-ca/)

This eclipse plugin provides a simple spring and JSF tooling integration. 
The plugin findes by using STS/SpringIDE all sping beans of a project and shows this beans
in the eclipse content assist of a JSF view e.g. in a XHTML file.  

The plugin only works in JSF eclipse web projects.

## Requirements

 - STS 2.8.1
 - or Eclipse Indigo 3.7.2 jee with installed spring ide

## Installation

 1. download the plugin and copy it to your eclipse's dropins folder 
    (from https://github.com/edmund-wagner/springsts-jsf-ca/downloads)
 2. restart eclipse (safety first ;)
 3. add your spring contexts to the project (project/properties/spring/beans support -> config files

A update site for the plugin is comming soon... 

## Additional Configuration

You can specify additional beans (without @component etc..) to be suggested in the jsfca pref page (window/preferences/jsfca)

define the base package to be scanned and a java regex for the class name.

eg:  "com.me.project.model" and  ".*Model"

## License

Apache License, Version 2.0 (current)
http://www.apache.org/licenses/LICENSE-2.0
