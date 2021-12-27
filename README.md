[![][ButlerImage]][Jenkins]

SemanticVersioning (for Jenkins)
==========

[![Jenkins](https://ci.jenkins.io/job/Plugins/job/semantic-versioning-plugin/job/master/badge/icon)](https://ci.jenkins.io/job/Plugins/job/semantic-versioning-plugin/job/master/)
[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/semantic-versioning-plugin.svg)](https://plugins.jenkins.io/semantic-versioning-plugin)
[![Jenkins Plugin Contributors](https://img.shields.io/github/contributors/jenkinsci/semantic-versioning-plugin.svg?color=blue)](https://github.com/jenkinsci/semantic-versioning-plugin/graphs/contributors)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/semantic-versioning-plugin.svg?color=blue&label=installations)](https://github.com/jenkinsci/semantic-versioning-plugin/graphs/contributors)
[![Jenkins Plugin Release](https://img.shields.io/github/release/jenkinsci/semantic-versioning-plugin.svg?label=changelog)](https://github.com/jenkinsci/semantic-versioning-plugin/releases/latest)


About
-----
The SemanticVersioning plugin for [Jenkins] is a plugin that reads the base version (Major, Minor, SNAPSHOT) settings 
from various build definition file formats. The plugin currently supports POM and SBT (Build.scala). 

SemanticVersioning uses the values parsed from the build definition files in conjunction with the 
[Jenkins] build number to create a [Semantic Versioning] string. 

The string is stored in an build-environment variable (name is configurable). Additionally the
 [Semantic Versioning] string is written to a file in the Artifact directory.

The second part of the plugin adds a column to the Dashboard that displays the [Semantic Versioning] string from the 
most recent successful build.

Pull / Feature Requests
-----
Both are welcome and will be prioritized and executed as time and resource allows.

[ButlerImage]: https://www.jenkins.io/sites/default/files/jenkins_logo.png
[MIT License]: https://github.com/jenkinsci/jenkins/raw/master/LICENSE.txt
[Jenkins]:https://www.jenkins.io/
[Semantic Versioning]:https://semver.org/
