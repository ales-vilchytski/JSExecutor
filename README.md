Simple wrapper for JavaScript JSR 223 engine. Main purpose of this class is to bring useful functionality like:
*including js-files while executing scripts to modularize source code, like '#include' in C or 'require' in Ruby.
*useful methods, implemented in Java and JS (*in progress*)

This project has overcomplicated structure with test and features subprojects.
It's made as example and sandbox for using tools like Ant and Ivy.
The most important classes: 
*by.ales.javascript.JSExecutor - wrapper for JSR-223
*by.ales.javascript.JSTestExecutor - Ant-task that can be used to run Rhinounit tests (placed under features)

Test directory contains tests for JSExecutor and .js files (runned via JSTestExecutor). Tests can be runned 
separately.

For some specific information see source code :)
