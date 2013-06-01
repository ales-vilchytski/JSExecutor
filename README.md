Sample wrapper for JavaScript JSR 223 (Java6) engine. 
---------------------------------------------

Main purpose of this classes is to bring example of use JS + Java and related tools (Ant, Ivy, JUnit etc)
This project has overcomplicated structure with test and features subprojects.
The most important classes: 
* <i>by.ales.javascript.JSExecutor</i> - wrapper for JSR-223
* <i>by.ales.javascript.JSTestExecutor</i> - Ant-task that can be used to run Rhinounit tests (placed under features)

Test directory contains tests for JSExecutor and .js files (runned via JSTestExecutor). 
Tests can be runned separately for Java and JS.

For specific information see source code :)

Example of use: <a href="https://github.com/ales-vilchytski/JMineSweeper">JMineSweeper</a>

P.S. Java 7 includes Rhino 1.7R3 which implements <a href="https://developer.mozilla.org/en-US/docs/New_in_Rhino_1.7R3">commonJS</a>, 
so this project is mostly useless in practical sense. 
