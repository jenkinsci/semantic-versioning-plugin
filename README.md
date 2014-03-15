[![][ButlerImage]][website] 

SemanticVersioning (for Jenkins)
==========

About
-----
The SemanticVersioning plugin for [Jenkins]([website]) is a plugin that reads the base version (Major, Minor, SNAPSHOT) settings from various build definition file formats. The plugin currently supports POM and SBT (Build.scala). SemanticVersioning uses the values parsed from the build definition files in conjunction with the [Jenkins]([website]) build number to create a [Semantic Versioning]([semver]) string. The string is stored in an build-environment variable (name is configurable). Additionally the [Semantic Versioning]([semver]) string is written to a file in the Artifact directory.

The second part of the plugin adds a column to the Dashboard that displays the [Semantic Versioning]([semver]) string from the most recent successful build.

Source
------
The latest and greatest source for the SemanticVersioning plugin can be found on [GitHub]. Fork us!

Pull / Feature Requests
-----
Both are welcome and will be prioritized and executed as time and resource allows. 

[ButlerImage]: http://jenkins-ci.org/sites/default/files/jenkins_logo.png
[MIT License]: https://github.com/jenkinsci/jenkins/raw/master/LICENSE.txt
[GitHub]: https://github.com/ciroque/SemanticVersioning
[website]: http://jenkins-ci.org
[semver]: http://semver.org/
