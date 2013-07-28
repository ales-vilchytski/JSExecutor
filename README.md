Examples of using JavaScript in Java application (Mozilla Rhino, JSR223)  
===================================

Main purpose of this project is to bring examples of using JS in Java. Some Java tools (Ant, Ivy, JUnit etc) are used too.
This project has overcomplicated (it's example) structure with 'test' and 'features' subprojects.
The most important classes: 
* [by.ales.javascript.RhinoExecutor](/src/by/ales/javascript/RhinoExecutor.java) - wrapper for Rhino using CommonJS.module feature
* [by.ales.javascript.JSR223Executor](/src/by/ales/javascript/JSExecutor.java) - wrapper for JSR-223 JavaScript engine
* [by.ales.javascript.JSTestExecutor](features/JSTestExecutor/src/by/ales/javascript/JSTestExecutor.java) - Ant-task that can be used to run Rhinounit tests (placed under features)

Test directory contains tests for all major Java classes and JS modules. Tests can be runned separately for Java and JS.

Example of using this project in Java desktop application: <a href="https://github.com/ales-vilchytski/JMineSweeper">JMineSweeper</a> 