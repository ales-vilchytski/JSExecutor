Simple wrapper for JavaScript JSR 223 engine. 
---------------------------------------------

Main purpose of this classes is to bring useful functionality like:
* including js-files while executing scripts to modularize source code, like '#include' in C or 'require' in Ruby.
* useful methods, implemented in Java and JS (<i>in progress</i>)

This project has overcomplicated structure with test and features subprojects.
It's made as example and sandbox for using tools like Ant and Ivy.
The most important classes: 
* <i>by.ales.javascript.JSExecutor</i> - wrapper for JSR-223
* <i>by.ales.javascript.JSTestExecutor</i> - Ant-task that can be used to run Rhinounit tests (placed under features)

Test directory contains tests for JSExecutor and .js files (runned via JSTestExecutor). Tests can be runned 
separately for Java and JS.

For specific information see source code :)
